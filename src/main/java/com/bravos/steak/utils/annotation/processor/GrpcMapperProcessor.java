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
import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import java.util.Set;

@SupportedAnnotationTypes("com.bravos.steak.utils.annotation.GrpcMapper")
@SupportedSourceVersion(SourceVersion.RELEASE_25)
public class GrpcMapperProcessor extends AbstractProcessor {

  private static final String TEMPLATE =
      """
          package %s;
          
          @org.mapstruct.Mapper(componentModel = "spring",
              collectionMappingStrategy = org.mapstruct.CollectionMappingStrategy.ADDER_PREFERRED,
              nullValueCheckStrategy = org.mapstruct.NullValueCheckStrategy.ALWAYS)
          public interface %s {
          
              %s INSTANCE = org.mapstruct.factory.Mappers.getMapper(%s.class);
          
              %s toGrpc(%s source);
          
              %s toModel(%s grpc);
          
          }
          """;

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    for(Element element : roundEnv.getElementsAnnotatedWith(GrpcMapper.class)) {
      if(!(element instanceof TypeElement classElement)) {
        continue;
      }
      String packageName = processingEnv.getElementUtils().getPackageOf(classElement).getQualifiedName().toString();
      String className = classElement.getSimpleName().toString();
      String mapperClassName = className + "GrpcMapper";
      TypeMirror targetType = getGrpcClassType(classElement);
      if (targetType == null) {
        processingEnv.getMessager().printMessage(
            Diagnostic.Kind.ERROR,
            "`grpcClass` not found on `@GrpcMapper`",
            element
        );
        continue;
      }
      TypeElement grpcTypeElement = (TypeElement) ((DeclaredType) targetType).asElement();
      String grpcClassName = grpcTypeElement.getQualifiedName().toString();
      try {
        JavaFileObject file = processingEnv.getFiler().createSourceFile(packageName + "." + mapperClassName, classElement);
        try (Writer writer = file.openWriter()) {
          writer.write(String.format(
              TEMPLATE,
              packageName,
              mapperClassName,
              mapperClassName,
              mapperClassName,
              grpcClassName,
              className,
              className,
              grpcClassName
          ));
        }
      } catch (IOException e) {
        processingEnv.getMessager().printMessage(
            Diagnostic.Kind.ERROR,
            "Failed to generate gRPC mapper: " + e.getMessage(),
            element
        );
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
