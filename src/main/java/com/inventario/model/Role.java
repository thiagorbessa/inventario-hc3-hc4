package com.inventario.model;

import jakarta.persistence.*;
import lombok.*;

public enum Role {
    ROLE_MASTER,
    ROLE_ADMIN,
    ROLE_VISUALIZADOR
}
