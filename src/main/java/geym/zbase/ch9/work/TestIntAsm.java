package geym.zbase.ch9.work;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * https://blog.csdn.net/mn960mn/article/details/51418236
 * http://blueyan.iteye.com/blog/2288286
 * http://www.wangyuwei.me/2017/01/19/JVM指令集整理/
 */
public class TestIntAsm extends ClassLoader implements Opcodes {
    public static void main(final String args[]) throws Exception {

        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        //声明一个类，使用JDK1.7版本，public的类，父类是java.lang.Object，没有实现任何接口
        cw.visit(Opcodes.V1_7, Opcodes.ACC_PUBLIC, "com/jvm/day8/asm/Example", null, "java/lang/Object", null);
        //初始化一个无参的构造函数
        MethodVisitor mw = cw.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);
        mw.visitVarInsn(Opcodes.ALOAD, 0); // this 入栈
        //执行父类的init初始化
        mw.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V");
        //从当前方法返回void
        mw.visitInsn(Opcodes.RETURN);
        mw.visitMaxs(0, 0);
        mw.visitEnd(); // 方法init结束


        // main方法开始
        mw = cw.visitMethod(Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC, "main", "([Ljava/lang/String;)V", null, null);

        //  int a=6; int b=7; int c=(a+b)*3;
        //  System.out.println(c);
        // 把变量放入局部变量表里
        //将单字节的常量值(-128~127)推送至栈顶(如果不是-128~127之间的数字，则不能用bipush指令)
        mw.visitIntInsn(Opcodes.BIPUSH, 6);
        // 将栈顶int型数值存入第1个本地变量
        mw.visitVarInsn(Opcodes.ISTORE, 1);
        mw.visitIntInsn(Opcodes.BIPUSH, 7);
        // 将栈顶int型数值存入第2个本地变量
        mw.visitVarInsn(Opcodes.ISTORE, 2);
        // 操作数栈
        //将第1个int型本地变量推送至栈顶
        mw.visitVarInsn(Opcodes.ILOAD, 1);
        //将第2个int型本地变量推送至栈顶
        mw.visitVarInsn(Opcodes.ILOAD, 2);
        //将栈顶两int型数值相加并将结果压入栈顶
        mw.visitInsn(Opcodes.IADD);
        mw.visitInsn(Opcodes.ICONST_3);
        //将栈顶两int型数值相乘并将结果压入栈顶
        mw.visitInsn(Opcodes.IMUL);
        // 将栈顶int型数值存入第3个本地变量
        mw.visitVarInsn(Opcodes.ISTORE, 3);
        //获取一个java.io.PrintStream对象
        mw.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        //将第3个int型本地变量推送至栈顶
        mw.visitVarInsn(Opcodes.ILOAD, 3);
        //执行println方法（执行的是参数为字符串，无返回值的println函数）
        mw.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(I)V");
        mw.visitInsn(Opcodes.RETURN);
        mw.visitMaxs(0, 0);

        mw.visitEnd(); // main方法结束

        final byte[] code = cw.toByteArray();

        TestIntAsm loader = new TestIntAsm();
        Class<?> exampleClass = loader.defineClass("com.jvm.day8.asm.Example", code, 0, code.length);
        exampleClass.getMethods()[0].invoke(null, new Object[]{null});

    }
}
