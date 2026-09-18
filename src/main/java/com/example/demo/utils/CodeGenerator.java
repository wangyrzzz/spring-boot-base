package com.example.demo.utils;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;

/**
 * MyBatis-Plus 3.5.x 代码生成器示例。
 * 运行前请通过系统属性替换数据库连接信息。
 */
public final class CodeGenerator {
    private CodeGenerator() {
    }

    public static void main(String[] args) {
        FastAutoGenerator.create(
                        System.getProperty("generator.url", "jdbc:mysql://localhost:3306/vote"),
                        System.getProperty("generator.username", "root"),
                        System.getProperty("generator.password", "root"))
                .globalConfig(builder -> builder.author("WangYuanrong").disableOpenDir().outputDir("src/main/java"))
                .packageConfig(builder -> builder.parent("com.example").moduleName("vote"))
                .strategyConfig(builder -> builder.addInclude("tb_rule", "tb_votee", "tb_vote_record")
                        .addTablePrefix("tb_")
                        .entityBuilder().enableLombok())
                .execute();
    }
}
