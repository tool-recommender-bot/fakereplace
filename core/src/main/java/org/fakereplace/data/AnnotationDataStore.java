package org.fakereplace.data;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javassist.bytecode.AccessFlag;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.AttributeInfo;
import javassist.bytecode.ClassFile;

import org.fakereplace.boot.GlobalClassDefinitionData;

/**
 * Stores information about the annotations on reloaded classes
 * 
 * @author stuart
 * 
 */
public class AnnotationDataStore
{

   static Map<Class<?>, Annotation[]> classAnnotations = Collections.synchronizedMap(new HashMap<Class<?>, Annotation[]>());

   static Map<Class<?>, Map<Class<? extends Annotation>, Annotation>> classAnnotationsByType = Collections.synchronizedMap(new HashMap<Class<?>, Map<Class<? extends Annotation>, Annotation>>());

   static Map<Field, Annotation[]> fieldAnnotations = Collections.synchronizedMap(new HashMap<Field, Annotation[]>());

   static Map<Field, Map<Class<? extends Annotation>, Annotation>> fieldAnnotationsByType = Collections.synchronizedMap(new HashMap<Field, Map<Class<? extends Annotation>, Annotation>>());

   static Map<Method, Annotation[]> methodAnnotations = Collections.synchronizedMap(new HashMap<Method, Annotation[]>());

   static Map<Method, Map<Class<? extends Annotation>, Annotation>> methodAnnotationsByType = Collections.synchronizedMap(new HashMap<Method, Map<Class<? extends Annotation>, Annotation>>());

   static Map<Method, Annotation[][]> parameterAnnotations = Collections.synchronizedMap(new HashMap<Method, Annotation[][]>());

   static Map<Constructor<?>, Annotation[]> constructorAnnotations = Collections.synchronizedMap(new HashMap<Constructor<?>, Annotation[]>());

   static Map<Constructor<?>, Map<Class<? extends Annotation>, Annotation>> constructorAnnotationsByType = Collections.synchronizedMap(new HashMap<Constructor<?>, Map<Class<? extends Annotation>, Annotation>>());

   static Map<Constructor<?>, Annotation[][]> constructorParameterAnnotations = Collections.synchronizedMap(new HashMap<Constructor<?>, Annotation[][]>());

   static public boolean isClassDataRecorded(Class<?> clazz)
   {
      return classAnnotations.containsKey(clazz);
   }

   static public Annotation[] getClassAnnotations(Class<?> clazz)
   {
      return classAnnotations.get(clazz);
   }

   static public Annotation getClassAnnotation(Class<?> clazz, Class<? extends Annotation> annotation)
   {
      return classAnnotationsByType.get(clazz).get(annotation);
   }

   static public boolean isClassAnnotationPresent(Class<?> clazz, Class<? extends Annotation> annotation)
   {
      return classAnnotationsByType.get(clazz).containsKey(annotation);
   }

   static public boolean isFieldDataRecorded(Field clazz)
   {
      return fieldAnnotations.containsKey(clazz);
   }

   static public Annotation[] getFieldAnnotations(Field clazz)
   {
      return fieldAnnotations.get(clazz);
   }

   static public Annotation getFieldAnnotation(Field clazz, Class<? extends Annotation> annotation)
   {
      return fieldAnnotationsByType.get(clazz).get(annotation);
   }

   static public boolean isFieldAnnotationPresent(Field clazz, Class<? extends Annotation> annotation)
   {
      return fieldAnnotationsByType.get(clazz).containsKey(annotation);
   }

   static public boolean isMethodDataRecorded(Method clazz)
   {
      return methodAnnotations.containsKey(clazz);
   }

   static public Annotation[] getMethodAnnotations(Method clazz)
   {
      return methodAnnotations.get(clazz);
   }

   static public Annotation getMethodAnnotation(Method clazz, Class<? extends Annotation> annotation)
   {
      return methodAnnotationsByType.get(clazz).get(annotation);
   }

   static public boolean isMethodAnnotationPresent(Method clazz, Class<? extends Annotation> annotation)
   {
      return methodAnnotationsByType.get(clazz).containsKey(annotation);
   }

   static public Annotation[][] getMethodParameterAnnotations(Method clazz)
   {
      return parameterAnnotations.get(clazz);
   }

   static Class<?> createAnnotationsProxy(ClassLoader loader, AnnotationsAttribute annotations)
   {
      String proxyName = GlobalClassDefinitionData.getProxyName();
      ClassFile proxy = new ClassFile(false, proxyName, "java.lang.Object");
      proxy.setAccessFlags(AccessFlag.PUBLIC);
      AttributeInfo a = annotations.copy(proxy.getConstPool(), Collections.EMPTY_MAP);
      proxy.addAttribute(a);
      try
      {
         ByteArrayOutputStream bytes = new ByteArrayOutputStream();
         DataOutputStream dos = new DataOutputStream(bytes);
         try
         {
            proxy.write(dos);
         }
         catch (IOException e)
         {
            throw new RuntimeException(e);
         }
         GlobalClassDefinitionData.saveProxyDefinition(loader, proxyName, bytes.toByteArray());
         return loader.loadClass(proxyName);
      }
      catch (ClassNotFoundException e)
      {
         throw new RuntimeException(e);
      }
   }

   static public void recordClassAnnotations(Class<?> clazz, AnnotationsAttribute annotations)
   {
      // no annotations
      if (annotations == null)
      {
         Annotation[] ans = new Annotation[0];
         classAnnotations.put(clazz, ans);
         classAnnotationsByType.put(clazz, Collections.EMPTY_MAP);
         return;
      }
      Class<?> pclass = createAnnotationsProxy(clazz.getClassLoader(), annotations);
      classAnnotations.put(clazz, pclass.getAnnotations());
      Map<Class<? extends Annotation>, Annotation> anVals = new HashMap<Class<? extends Annotation>, Annotation>();
      classAnnotationsByType.put(clazz, anVals);
      int count = 0;
      for (Annotation a : pclass.getAnnotations())
      {
         anVals.put(a.annotationType(), a);
         count++;
      }
   }

   static public void recordFieldAnnotations(Field field, AnnotationsAttribute annotations)
   {
      // no annotations
      if (annotations == null)
      {
         Annotation[] ans = new Annotation[0];
         fieldAnnotations.put(field, ans);
         fieldAnnotationsByType.put(field, Collections.EMPTY_MAP);
         return;
      }
      Class<?> pclass = createAnnotationsProxy(field.getDeclaringClass().getClassLoader(), annotations);
      fieldAnnotations.put(field, pclass.getAnnotations());
      Map<Class<? extends Annotation>, Annotation> anVals = new HashMap<Class<? extends Annotation>, Annotation>();
      fieldAnnotationsByType.put(field, anVals);
      int count = 0;
      for (Annotation a : pclass.getAnnotations())
      {
         anVals.put(a.annotationType(), a);
         count++;
      }
   }

   static public void recordMethodAnnotations(Method method, AnnotationsAttribute annotations)
   {
      // no annotations
      if (annotations == null)
      {
         Annotation[] ans = new Annotation[0];
         methodAnnotations.put(method, ans);
         methodAnnotationsByType.put(method, Collections.EMPTY_MAP);
         return;
      }
      Class<?> pclass = createAnnotationsProxy(method.getDeclaringClass().getClassLoader(), annotations);
      methodAnnotations.put(method, pclass.getAnnotations());
      Map<Class<? extends Annotation>, Annotation> anVals = new HashMap<Class<? extends Annotation>, Annotation>();
      methodAnnotationsByType.put(method, anVals);
      int count = 0;
      for (Annotation a : pclass.getAnnotations())
      {
         anVals.put(a.annotationType(), a);
         count++;
      }
   }

   static public void recordConstructorAnnotations(Constructor<?> constructor, AnnotationsAttribute annotations)
   {
      // no annotations
      if (annotations == null)
      {
         Annotation[] ans = new Annotation[0];
         constructorAnnotations.put(constructor, ans);
         constructorAnnotationsByType.put(constructor, Collections.EMPTY_MAP);
         return;
      }
      Class<?> pclass = createAnnotationsProxy(constructor.getDeclaringClass().getClassLoader(), annotations);
      constructorAnnotations.put(constructor, pclass.getAnnotations());
      Map<Class<? extends Annotation>, Annotation> anVals = new HashMap<Class<? extends Annotation>, Annotation>();
      constructorAnnotationsByType.put(constructor, anVals);
      int count = 0;
      for (Annotation a : pclass.getAnnotations())
      {
         anVals.put(a.annotationType(), a);
         count++;
      }

   }
}
