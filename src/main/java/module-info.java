module RetoConjuntoObjectDBJavaFX {
    // 1. Módulos de Interfaz Gráfica
    requires javafx.controls;
    requires javafx.fxml;
    requires static lombok;

    // 2. Módulos de Persistencia (ObjectDB y Jakarta)
    requires jakarta.persistence;
    requires com.objectdb;
    requires java.naming;

    // 3. Abrir paquetes para JavaFX (Carga de FXML)
    // Nota: Abre 'controllers' si tus archivos FXML buscan allí sus controladores
    opens controllers to javafx.fxml;
    opens utils to javafx.fxml;

    // 4. Abrir entidades para el motor de ObjectDB y JPA [cite: 99, 107]
    // Esto permite que ObjectDB acceda a los campos mediante reflexión [cite: 7, 10]
    opens user to jakarta.persistence, com.objectdb;
    opens pelicula to jakarta.persistence, com.objectdb;
    opens copiaPelicula to jakarta.persistence, com.objectdb;

    // 5. Exportar paquetes para que sean visibles por otros módulos
    exports utils;
    exports controllers;
    exports user;
    exports pelicula;
    exports copiaPelicula;
    exports session;
}