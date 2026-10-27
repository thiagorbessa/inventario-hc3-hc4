package com.inventario.service;

import com.inventario.model.Equipamento;
import com.inventario.repository.EquipamentoRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.apache.poi.openxml4j.util.ZipSecureFile;

import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PlanilhaService {

    @Autowired
    private EquipamentoRepository equipamentoRepository;

    // ==========================================
    // 1. IMPORTAR E MESCLAR (EXCEL DE MÚLTIPLAS ABAS)
    // ==========================================
    public void importarDeExcel(MultipartFile file) throws Exception {
        ZipSecureFile.setMinInflateRatio(0.001);
        Workbook workbook = new XSSFWorkbook(file.getInputStream());
        int totalAbas = workbook.getNumberOfSheets();

        for (int i = 0; i < totalAbas; i++) {
            Sheet sheet = workbook.getSheetAt(i);
            String nomeAba = sheet.getSheetName();
            Iterator<Row> rows = sheet.iterator();
            if (!rows.hasNext()) continue;

            Row headerRow = rows.next();
            Map<String, Integer> colMap = mapearCabecalho(headerRow);

            if (!colMap.containsKey("netname") && !colMap.containsKey("patrimoniomicro") && !colMap.containsKey("patmicro")) continue;

            while (rows.hasNext()) {
                Row currentRow = rows.next();
                processarLinha(colMap, new AdaptadorLinhaRow(currentRow), nomeAba);
            }
        }
        workbook.close();
    }

    // ==========================================
    // 2. IMPORTAR DE TEXTO COPIADO / COLADO
    // ==========================================
    public void importarDeTextoColado(String textoColado) {
        if (textoColado == null || textoColado.trim().isEmpty()) return;
        String[] linhas = textoColado.split("\r?\n");
        if (linhas.length < 2) return;

        String[] cabecalho = linhas[0].split("\t");
        Map<String, Integer> colMap = new HashMap<>();
        for (int i = 0; i < cabecalho.length; i++) {
            colMap.put(limparTexto(cabecalho[i]), i);
        }

        for (int i = 1; i < linhas.length; i++) {
            String linha = linhas[i].trim();
            if (linha.isEmpty()) continue;
            processarLinha(colMap, new AdaptadorLinhaArray(linha.split("\t", -1)), "");
        }
    }

    // ==========================================
    // LÓGICA DE MESCLAGEM E RECONHECIMENTO DE DADOS
    // ==========================================
    private void processarLinha(Map<String, Integer> colMap, AdaptadorLinha linha, String nomeAba) {
        String netname = getValor(colMap, linha, "netname", "hostname", "nome");

        // VALIDAÇÃO: Se não começar com "HC", descarta a linha
        if (netname.isEmpty() || !netname.toUpperCase().startsWith("HC")) {
            return;
        }

        String patrimonio = getValor(colMap, linha, "patrimoniomicro", "patmicro", "patrimonio", "pat.micro");
        boolean patrimonioValido = !patrimonio.isEmpty() && patrimonio.matches(".*\\d+.*");

        Equipamento eq = null;
        if (patrimonioValido) {
            eq = equipamentoRepository.findById(patrimonio).orElse(null);
        }
        if (eq == null) {
            List<Equipamento> duplicados = equipamentoRepository.findByNetnameIgnoreCase(netname);
            if (!duplicados.isEmpty()) {
                eq = duplicados.get(0); // Pega o primeiro registro encontrado
            }
        }

        if (eq == null) {
            eq = new Equipamento();
            eq.setPatrimonioMicro(patrimonioValido ? patrimonio : "PROV-" + UUID.randomUUID().toString().substring(0, 8));
        }

        String unidade = getValor(colMap, linha, "unidade", "lotacao", "unid", "ção");
        if (!unidade.isEmpty()) {
            eq.setUnidade(unidade.toUpperCase().contains("HC4") ? "HC4" : "HC3");
        } else {
            eq.setUnidade(netname.toUpperCase().startsWith("HC4") ? "HC4" : "HC3");
        }

        eq.setNetname(netname);

        String expressService = getValor(colMap, linha, "expressservice", "expressservicetag");
        if (!expressService.isEmpty()) eq.setExpressService(expressService);

        String serviceTag = getValor(colMap, linha, "servicetag", "st");
        if (!serviceTag.isEmpty()) eq.setServiceTag(serviceTag);

        String modeloMicro = getValor(colMap, linha, "modelomicro", "modelo");
        if (!modeloMicro.isEmpty()) eq.setModeloMicro(modeloMicro);

        String patMonitor = getValor(colMap, linha, "patrimoniomonitor", "patmonitor", "pat.monitor");
        if (!patMonitor.isEmpty()) eq.setPatrimonioMonitor(patMonitor);

        String serieMonitor = getValor(colMap, linha, "seriemonitor");
        if (!serieMonitor.isEmpty()) eq.setSerieMonitor(serieMonitor);

        String mac = getValor(colMap, linha, "macaddress", "mac");
        if (!mac.isEmpty()) eq.setMacAddress(mac);

        String local = getValor(colMap, linha, "local", "localizacao", "setor");
        if (!local.isEmpty()) eq.setLocal(local);

        String ramal = getValor(colMap, linha, "ramal", "telefone");
        if (!ramal.isEmpty()) eq.setRamal(ramal);

        String ipMicro = getValor(colMap, linha, "ipmicro", "ip");
        if (!ipMicro.isEmpty()) eq.setIpMicro(ipMicro);

        String ipSwitch = getValor(colMap, linha, "ipswitch", "switchip");
        if (!ipSwitch.isEmpty()) eq.setIpSwitch(ipSwitch);

        String portaSwitch = getValor(colMap, linha, "portaswitch", "porta");
        if (!portaSwitch.isEmpty()) eq.setPortaSwitch(portaSwitch);

        String patchPanel = getValor(colMap, linha, "patchpanel", "panel", "patch");
        if (!patchPanel.isEmpty()) eq.setPatchPanel(patchPanel);

        String obs = getValor(colMap, linha, "obs", "observacao", "observacoes");
        if (!obs.isEmpty()) eq.setObs(obs);

        // SEPARAÇÃO RIGOROSA: ANDAR x RACK
        String andar = getValor(colMap, linha, "andar");
        if (andar.isEmpty() && nomeAba != null && !nomeAba.trim().isEmpty()) {
            andar = nomeAba.trim();
        }
        if (!andar.isEmpty()) {
            eq.setAndar(andar);
        }

        String rack = getValor(colMap, linha, "rack");
        if (!rack.isEmpty()) {
            eq.setRack(rack);
        }

        equipamentoRepository.save(eq);
    }

    // ==========================================
    // EXPORTAR PARA EXCEL (SEM FILTROS)
    // ==========================================
    public void exportarParaExcel(OutputStream outputStream) throws Exception {
        exportarParaExcelFiltrado(null, null, null, null, null, null, outputStream);
    }

    // ==========================================
    // EXPORTAR PARA EXCEL (APENAS RESULTADOS FILTRADOS)
    // ==========================================
    public void exportarParaExcelFiltrado(String unidade, String netname, String modelo,
                                          String local, String andar, String geral,
                                          OutputStream outputStream) throws Exception {
        List<Equipamento> todos = equipamentoRepository.findAll();

        // Aplica o filtro nos dados antes de gerar o Excel
        List<Equipamento> equipamentos = todos.stream().filter(eq -> {
            boolean matchUnidade = (unidade == null || unidade.trim().isEmpty()) ||
                    (eq.getUnidade() != null && eq.getUnidade().equalsIgnoreCase(unidade.trim()));

            boolean matchNetname = (netname == null || netname.trim().isEmpty()) ||
                    (eq.getNetname() != null && eq.getNetname().toLowerCase().contains(netname.trim().toLowerCase()));

            boolean matchModelo = (modelo == null || modelo.trim().isEmpty()) ||
                    (eq.getModeloMicro() != null && eq.getModeloMicro().toLowerCase().contains(modelo.trim().toLowerCase()));

            boolean matchLocal = (local == null || local.trim().isEmpty()) ||
                    (eq.getLocal() != null && eq.getLocal().toLowerCase().contains(local.trim().toLowerCase()));

            boolean matchAndar = (andar == null || andar.trim().isEmpty()) ||
                    (eq.getAndar() != null && eq.getAndar().toLowerCase().contains(andar.trim().toLowerCase()));

            boolean matchGeral = (geral == null || geral.trim().isEmpty()) ||
                    linhaContemTexto(eq, geral.trim().toLowerCase());

            return matchUnidade && matchNetname && matchModelo && matchLocal && matchAndar && matchGeral;
        }).collect(Collectors.toList());

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Inventário TI");

        Row headerRow = sheet.createRow(0);
        String[] colunas = {"Unidade", "Patrimônio Micro", "Netname", "Express Service", "Service Tag",
                "Modelo Micro", "Patrimônio Monitor", "Série Monitor", "MAC Address",
                "Local", "Andar", "Ramal", "IP Micro", "IP Switch", "Porta Switch", "Rack", "Patch Panel", "Obs"};

        for (int i = 0; i < colunas.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(colunas[i]);
            CellStyle style = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            style.setFont(font);
            cell.setCellStyle(style);
        }

        int rowIdx = 1;
        for (Equipamento eq : equipamentos) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(eq.getUnidade() != null ? eq.getUnidade() : "");
            row.createCell(1).setCellValue(eq.getPatrimonioMicro() != null ? eq.getPatrimonioMicro() : "");
            row.createCell(2).setCellValue(eq.getNetname() != null ? eq.getNetname() : "");
            row.createCell(3).setCellValue(eq.getExpressService() != null ? eq.getExpressService() : "");
            row.createCell(4).setCellValue(eq.getServiceTag() != null ? eq.getServiceTag() : "");
            row.createCell(5).setCellValue(eq.getModeloMicro() != null ? eq.getModeloMicro() : "");
            row.createCell(6).setCellValue(eq.getPatrimonioMonitor() != null ? eq.getPatrimonioMonitor() : "");
            row.createCell(7).setCellValue(eq.getSerieMonitor() != null ? eq.getSerieMonitor() : "");
            row.createCell(8).setCellValue(eq.getMacAddress() != null ? eq.getMacAddress() : "");
            row.createCell(9).setCellValue(eq.getLocal() != null ? eq.getLocal() : "");
            row.createCell(10).setCellValue(eq.getAndar() != null ? eq.getAndar() : "");
            row.createCell(11).setCellValue(eq.getRamal() != null ? eq.getRamal() : "");
            row.createCell(12).setCellValue(eq.getIpMicro() != null ? eq.getIpMicro() : "");
            row.createCell(13).setCellValue(eq.getIpSwitch() != null ? eq.getIpSwitch() : "");
            row.createCell(14).setCellValue(eq.getPortaSwitch() != null ? eq.getPortaSwitch() : "");
            row.createCell(15).setCellValue(eq.getRack() != null ? eq.getRack() : "");
            row.createCell(16).setCellValue(eq.getPatchPanel() != null ? eq.getPatchPanel() : "");
            row.createCell(17).setCellValue(eq.getObs() != null ? eq.getObs() : "");
        }

        for (int i = 0; i < colunas.length; i++) {
            sheet.autoSizeColumn(i);
        }

        workbook.write(outputStream);
        workbook.close();
    }

    private boolean linhaContemTexto(Equipamento eq, String g) {
        String concat = String.format("%s %s %s %s %s %s %s %s %s %s %s %s %s %s %s %s %s %s",
                eq.getUnidade() != null ? eq.getUnidade() : "",
                eq.getPatrimonioMicro() != null ? eq.getPatrimonioMicro() : "",
                eq.getNetname() != null ? eq.getNetname() : "",
                eq.getExpressService() != null ? eq.getExpressService() : "",
                eq.getServiceTag() != null ? eq.getServiceTag() : "",
                eq.getModeloMicro() != null ? eq.getModeloMicro() : "",
                eq.getPatrimonioMonitor() != null ? eq.getPatrimonioMonitor() : "",
                eq.getSerieMonitor() != null ? eq.getSerieMonitor() : "",
                eq.getMacAddress() != null ? eq.getMacAddress() : "",
                eq.getLocal() != null ? eq.getLocal() : "",
                eq.getAndar() != null ? eq.getAndar() : "",
                eq.getRamal() != null ? eq.getRamal() : "",
                eq.getIpMicro() != null ? eq.getIpMicro() : "",
                eq.getIpSwitch() != null ? eq.getIpSwitch() : "",
                eq.getPortaSwitch() != null ? eq.getPortaSwitch() : "",
                eq.getRack() != null ? eq.getRack() : "",
                eq.getPatchPanel() != null ? eq.getPatchPanel() : "",
                eq.getObs() != null ? eq.getObs() : ""
        ).toLowerCase();
        return concat.contains(g);
    }

    // --- UTILITÁRIOS ---
    private Map<String, Integer> mapearCabecalho(Row headerRow) {
        Map<String, Integer> map = new HashMap<>();
        for (Cell cell : headerRow) {
            String nomeColuna = limparTexto(getCellValue(cell));
            if (!nomeColuna.isEmpty()) map.put(nomeColuna, cell.getColumnIndex());
        }
        return map;
    }

    private String getValor(Map<String, Integer> colMap, AdaptadorLinha linha, String... nomesPossiveis) {
        for (String nome : nomesPossiveis) {
            Integer idx = colMap.get(limparTexto(nome));
            if (idx != null) {
                String val = linha.getValor(idx);
                if (val != null && !val.trim().isEmpty()) return val.trim();
            }
        }
        return "";
    }

    private String limparTexto(String texto) {
        return texto == null ? "" : texto.toLowerCase().replaceAll("[^a-z0-9]", "");
    }

    private String getCellValue(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING: return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) return cell.getDateCellValue().toString();
                return String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN: return String.valueOf(cell.getBooleanCellValue());
            default: return "";
        }
    }

    private interface AdaptadorLinha { String getValor(int index); }
    private class AdaptadorLinhaRow implements AdaptadorLinha {
        private final Row row;
        public AdaptadorLinhaRow(Row row) { this.row = row; }
        public String getValor(int index) { return getCellValue(row.getCell(index)); }
    }
    private class AdaptadorLinhaArray implements AdaptadorLinha {
        private final String[] valores;
        public AdaptadorLinhaArray(String[] valores) { this.valores = valores; }
        public String getValor(int index) { return (index >= 0 && index < valores.length) ? valores[index] : ""; }
    }
}