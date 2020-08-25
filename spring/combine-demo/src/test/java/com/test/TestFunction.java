package com.test;


import java.util.function.Function;

public class TestFunction {
    public static void main(String[] args) {
        // Function<T, R>  Function是一个泛型类，其中定义了两个泛型参数T和R，在Function中，T代表输入参数，R代表返回的结果。
        Function<Integer,Integer> test= i->i+1;
        //Function 就是一个函数，其作用类似于数学中函数,apply就是去执行这个函数，并返回结果
        int n= test.apply(5);
        System.out.println(n);
    }
}
