package ru.clevertec.gradle;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.testing.Test;
import org.gradle.testing.jacoco.plugins.JacocoPlugin;
import org.gradle.testing.jacoco.tasks.JacocoReport;
import org.gradle.testing.jacoco.plugins.JacocoPluginExtension;
import org.gradle.api.tasks.SourceSetContainer;

public class JacocoReportPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        project.getPlugins().apply(JacocoPlugin.class);

        project.getExtensions().configure(JacocoPluginExtension.class, (JacocoPluginExtension jacoco) -> {
            jacoco.setToolVersion("0.8.12");
        });

        project.getTasks().register("jacocoTestReport", JacocoReport.class, jacocoTestReport -> {
            jacocoTestReport.dependsOn(project.getTasks().withType(Test.class));

            jacocoTestReport.getReports().getHtml().getRequired().set(true);

            jacocoTestReport.getSourceDirectories().setFrom(project.files(project.getExtensions().getByType(SourceSetContainer.class).getByName("main").getAllSource().getSrcDirs()));
            jacocoTestReport.getClassDirectories().setFrom(project.files(project.getExtensions().getByType(SourceSetContainer.class).getByName("main").getOutput()));
            jacocoTestReport.getExecutionData().setFrom(project.fileTree(project.getProjectDir()).include("**/build/jacoco/*.exec"));
        });

        project.getTasks().withType(Test.class).configureEach(test ->
                test.finalizedBy("jacocoTestReport")
        );
    }
}
