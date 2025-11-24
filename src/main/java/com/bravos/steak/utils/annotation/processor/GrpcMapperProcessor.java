package com.bravos.steak.utils.annotation.processor;

import com.bravos.steak.utils.annotation.GrpcMapper;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.Writer;
import java.util.Map;
import java.util.Set;

@SupportedAnnotationTypes("com.bravos.steak.utils.annotation.GrpcMapper")
@SupportedSourceVersion(SourceVersion.RELEASE_25)
public class GrpcMapperProcessor extends AbstractProcessor {

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    for (Element element : roundEnv.getElementsAnnotatedWith(GrpcMapper.class)) {
      if (!(element instanceof TypeElement classElement)) {
        continue;
      }

      String packageName = processingEnv.getElementUtils()
          .getPackageOf(classElement)
          .getQualifiedName()
          .toString();
      String domainClassName = classElement.getSimpleName().toString();
      String mapperClassName = domainClassName + "GrpcMapper";

      TypeMirror grpcType = getGrpcClassType(classElement);
      if (grpcType == null) {
        processingEnv.getMessager().printMessage(
            Diagnostic.Kind.ERROR,
            "`grpcClass` not found on `@GrpcMapper`",
            element
        );
        continue;
      }

      TypeElement grpcTypeElement = (TypeElement) ((DeclaredType) grpcType).asElement();
      String grpcClassName = grpcTypeElement.getQualifiedName().toString();
      String grpcSimpleName = grpcTypeElement.getSimpleName().toString();

      try {
        JavaFileObject file = processingEnv.getFiler()
            .createSourceFile(packageName + "." + mapperClassName, classElement);
        try (Writer writer = file.openWriter()) {
          writer.write("package " + packageName + ";\n\n");
          writer.write("import " + grpcClassName + ";\n");
          writer.write("import " + packageName + "." + domainClassName + ";\n");
          writer.write("import org.mapstruct.Mapper;\n");
          writer.write("import org.mapstruct.factory.Mappers;\n\n");
          writer.write("@Mapper\n");
          writer.write("public interface " + mapperClassName + " {\n");
          writer.write("    " + mapperClassName + " INSTANCE = Mappers.getMapper(" + mapperClassName + ".class);\n\n");
          writer.write("    " + domainClassName + " toModel(" + grpcSimpleName + " request);\n");
          writer.write("    " + grpcSimpleName + " toGrpc(" + domainClassName + " model);\n");
          writer.write("}\n");
        }
      } catch (Exception e) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage(), element);
      }
    }
    return true;
  }

  private TypeMirror getGrpcClassType(TypeElement classElement) {
    for (AnnotationMirror mirror : classElement.getAnnotationMirrors()) {
      if (!((TypeElement) mirror.getAnnotationType().asElement())
          .getQualifiedName()
          .contentEquals(GrpcMapper.class.getCanonicalName())) {
        continue;
      }
      for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry
          : mirror.getElementValues().entrySet()) {
        if (entry.getKey().getSimpleName().contentEquals("value")) {
          Object v = entry.getValue().getValue();
          if (v instanceof TypeMirror typeMirror) {
            return typeMirror;
          }
        }
      }
    }
    return null;
  }
}
