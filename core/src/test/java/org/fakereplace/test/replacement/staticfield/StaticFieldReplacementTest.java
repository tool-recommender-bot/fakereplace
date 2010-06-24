package org.fakereplace.test.replacement.staticfield;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;

import org.fakereplace.test.util.ClassReplacer;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class StaticFieldReplacementTest
{

   @BeforeClass
   public void setup()
   {
      ClassReplacer r = new ClassReplacer();
      r.queueClassForReplacement(StaticFieldClass.class, StaticFieldClass1.class);
      r.replaceQueuedClasses(true);
   }

   @Test
   public void testStaticFieldReplacement() throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException
   {

      Long v = StaticFieldClass.incAndGet();
      assert v.equals(new Long(1)) : "expected 1, got " + v;
      v = StaticFieldClass.incAndGet();
      assert v.equals(new Long(2)) : "expected 2, got " + v;
   }

   @Test
   public void testAddedStaticFieldGetDeclaredFields()
   {
      Field[] fields = StaticFieldClass.class.getDeclaredFields();
      boolean removedField = false;
      boolean longField = false;
      boolean list = false;
      for (Field f : fields)
      {
         if (f.getName().equals("removedField"))
         {
            removedField = true;
         }
         if (f.getName().equals("longField"))
         {
            longField = true;
         }
         if (f.getName().equals("list"))
         {
            list = true;
         }
      }
      assert list;
      assert longField;
      assert !removedField;
   }

   @Test
   public void testAddedStaticFieldGetFields()
   {
      Field[] fields = StaticFieldClass.class.getFields();
      boolean removedField = false;
      boolean longField = false;
      boolean list = false;
      for (Field f : fields)
      {
         if (f.getName().equals("removedField"))
         {
            removedField = true;
         }
         if (f.getName().equals("longField"))
         {
            longField = true;
         }
         if (f.getName().equals("list"))
         {
            list = true;
         }
      }
      assert !list;
      assert longField;
      assert !removedField;
   }

   @Test
   public void testStaticFieldGenericType() throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchFieldException
   {
      Field f = StaticFieldClass.class.getDeclaredField("list");
      assert ((ParameterizedType) f.getGenericType()).getActualTypeArguments()[0].equals(String.class);
   }

   @Test(expectedExceptions = NoSuchFieldException.class)
   public void testStaticFieldGetFieldNonPublicFieldsNotAccessible() throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchFieldException
   {
      Field f = StaticFieldClass.class.getField("list");
   }

}
