package com.deu.java.backend;
import io.javalin.Javalin;

public class Backend {

    public static void main(String[] args) {
        // Javalin 서버 설정
        var app = Javalin.create(/*config*/)
            .get("/", ctx -> ctx.result("Hello World"))
            .start(7070);
    }
}
