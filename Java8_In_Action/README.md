# 《JAVA 8 实战》

通过这本书了解 JAVA 8 的新特性和新的API的用法及一些高效的用法

[TOC]

## 第一部分 基础知识

### 1. 为什么关心Java 8

本章主要都是一些介绍：

> - Java 怎么又变了 
> - 日新月异的计算机应用背景：多核和处理大型数据集（大数据）
> - 改进的压力：函数式比命令式更适应新的体系架构
> - Java 8 的核心新特性：Lambda（匿名函数）、流（Stream API）、默认方法



需要懂一个 概念/名词 **谓词**

> 什么是谓词？
>
> 谓词( predicate )：在计算机语言的环境下，谓词是指条件表达式的求值返回真或假的过程。



#### 小结

> 一下是从本章学到的关键概念：
>
> - 请记住语言生态系统的思想，以及语言面临的 “要么改变，要么衰亡” 的压力。虽然 Java 可能现在非常有活力，但你可以回忆一下其他曾经也有活力但未能及时改进的语言的命运，如[COBOL](https://baike.baidu.com/item/COBOL) 。
> - Java 8 中新增的核心内容提供了令人激动的新概念和功能，方便我们编写既有效又简洁的程序。
> - 现有 Java 编程实践并不能很好地利用多核处理器。（也就是说我在看这本书之前可能一直在用单核模式）
> - 函数是一等值（操作的值是**一等值**，很多 Java 概念（方法和类等）为**二等值**， 这里的函数是一等值也就是将**方法（匿名方法）化为函数**）；记得方法如何作为函数式值来传递，还有 Lambda 是怎样写的。
> - Java 8 中 Streams(流) 的概念使得 Collections(集合) 的许多方面得以推广，让代码更为易读，并允许并行处理流元素。
> - 你可以在接口中使用默认方法，在实现类没有方法时提供方法内容。
> - 其他来自函数式编程的有趣思想，包括处理 null 和使用模式匹配。

### 2.通过行为参数化传递代码 

这一章通过一个原始的解决方法一步步的优化代码，每一步的优化都有介绍

##### 问题：筛选苹果

###### 1. 第一次尝试：筛选绿苹果

``` java
public static List<Apple> filterGreenApples(List<Apple> inventory) {
  	List<Apple> result = new ArrayList<>();
  	for(Apple apple : result) {
        if( "green".equals(apple.getColor()) ) {
            result.add(apple);
        }
  	}
  return result;
}
```

第一个解决方案可能是这样的，但是如果还要筛选红色的、浅绿色的、黄色的等等，难道就复制这个方法改一下方面名颜色和if条件吗？

一个良好的原则是在编写类似的代码之后，尝试将其抽象化。

###### 2. 第二次尝试：把颜色作为参数

``` java
public static List<Apple> filterApplesByColor(List<Apple> inventory, String color) {
  	List<Apple> result = new ArrayList<>();
  	for(Apple apple : result) {
        if( apple.getColor().equals(color) ) {
            result.add(apple);
        }
  	}
  return result;
}
```

现在只要像下面这样调用方法就可以了：

``` java
List<Apple> greenApples = filterApplesByColor(inventory, "green");
List<Apple> redApples = filterApplesByColor(inventory, "red");
```

太简单了对吧？那如果还要进行重量的筛选怎么办（重的苹果大于150克）

哼哼，聪明的你肯定早就想到还要进行重量的筛选，于是你写了下面的代码

``` java
public static List<Apple> filterApplesByWeight(List<Apple> inventory, int weight) {
  	List<Apple> result = new ArrayList<>();
  	for(Apple apple : result) {
        if( apple.getWeight() > weight ) {
            result.add(apple);
        }
  	}
  return result;
}
```

这个方案不错，但是请注意这个和上面的代码有很大部分的类似，这不符合软件工程原则**DRY( Don't Repeat Yourself，不要重复自己 )**，

你可以结合这两个为一个方法 filter，你可以加上一个标志来选择筛选哪种属性颜色或者重量（请千万不要这样做，后面很快会解释为什么！）。

###### 3. 第三次尝试：对能想到的每个属性做筛选

``` java
public static List<Apple> filterApples(List<Apple> inventory,String color, int weight, boolean flag) {
  	List<Apple> result = new ArrayList<>();
  	for(Apple apple : result) {
        if( (flag && apple.getColor().equals(color)) || (!flag && apple.getWeight() > weight) ) {
            result.add(apple);
        }
  	}
  return result;
}
```

你可以这么用（但真的很笨挫）：

``` java
List<Apple> greenApples = filterApples(inventory, "green", 0, true);
List<Apple> redApples = filterApples(inventory, "", 150, false);
```

这个解决方法糟透了。首先 true 和 false 是什么？此外这个方法非常不灵活，如果又有别的需求怎么办？

下面 **行为参数化** 介绍如何实现这种灵活性



我们需要根据 Apple 的某些属性（颜色、重量）来返回一个 boolean 值。我们把他称为**谓词（即一个返回boolean值的函数）**。

来定义一个接口对选择标准建模：

``` java
pulbic interface ApplePredicate {
  boolean test (Apple apple);
}
```

现在就可以用 ApplePredicate 的多个实现代表不同的选择标准了：

``` java
public class AppleHeavyWeightPredicate implements ApplePredicate {
  public boolean test(Apple apple) {
    return apple.getWeight() > 150;
  }
}

public class AppleGreenColorPredicate implements ApplePredicate {
  public boolean test(Apple apple) {
    return "grren".equals(apple.getColor());
  }
}
```

可以把这些标准看作 filter 方法的不同行为。刚刚这些和 “策略设计模式” 相关，它让你定义一族算法，把他们封装起来（称为 “策略”），然后在运行时选择一个算法。这里算法族是 ApplePredicate，不同的策略就是 AppleHeavyWeightPredicate 和 AppleGreenColorPredicate。



###### 4. 第四次尝试：根据抽象条件筛选

``` java
public static List<Apple> filterApples(List<Apple> inventory, ApplePredicate p){
  List<Apple> result = new ArrayList<>();
  for(Apple apple : inventory){
    if(p.test(apple)){
      result.add(apple);
    }
  }
  return result;
}
```

**1. 传递代码/行为**

现在比第一次的代码好多了，可以这样用

``` java
public static class AppleRedAndHeavyPredicate implements ApplePredicate{
  public boolean test(Apple apple){
    return "red".equals(apple.getColor()) && apple.getWeight() > 150; 
  }
}

List<Apple> redAndHeavyApples = filter(inventory, new AppleRedAndHeavyPredicate());
```

filterApples方法的行为取决于你通过ApplePredicate对象传递的代码。换句话说，就是把filterApples方法的行为参数化了！

**2. 多种行为，一个参数**

###### 5. 第五次尝试：使用匿名类

> **匿名类**
>
> 匿名类和你熟悉的Java 局部类（块中定义的类）差不多，但匿名类没有名字。它允许你声明生命并实例化一个类。换句话说，允许你随用随建。

``` java
List<Apple> redApples2 = filter(inventory, new ApplePredicate() {
  public boolean test(Apple a){
    return a.getColor().equals("red"); 
  }
});
```

就是将方法作为参数（可能有点不准确）。

###### 6. 第六次尝试：使用 Lambda 表达式

``` java
List<Apple> redApples2 = filter(inventory, (Apple a) -> "red".equals(a.getColor()));
```

这真的干净了不少。

###### 7. 第七次尝试：将 List 类型抽象化

``` java
public interface Predicate<T> {
  boolean test (T t);
}

public static <T> List<T> filter(List<T> list, Predicate<T> p) {
  List<T> result = new ArrayList<>();
  for(T e : list) {
    if(p.test(e)) {
      result.add(e);
    }
  }
  return result;
}
```

这样就可以像下面这样用了：

``` java
List<Apple> redApples = filter(inventory, (Apple apple) -> "red".equals(apple.getColor()) );
List<String> evenNumbers = filter(numbers, (Integer i) -> i%2 == 0 );
```

看这样你就可以用在任意的类型上了，只要改变条件和引用类型！

#### 小结

> - 行为参数化，就是一个方法接受多个不同的行为作为参数，并在内部使用它们，完成不同行为的能力。
> - 行为参数化可让代码更好地适应不断变化的要求，减轻未来的工作量。
> - 传递代码，就是将新行为作为参数传递给方法。但在 Java 8 之前这实现起来很啰嗦。为接口声明许多只用一次的实体类而造成的啰嗦代码，在 Java 8 之前可以用匿名类来减少。
> - Java API 包含很多可以用不同行为进行参数化的方法，包括排序、线程和 GUI 处理。

### 3. Lambda 表达式

本章详细的介绍了 Lambda 表达式

> Lambda 表达式可以理解为简洁地表示可传递的匿名函数的一种方式。
>
> - 匿名——我们说匿名，是因为它不像普通方法那样有一个明确的名称：写得少而想得多！
>
> - 函数——我们说他是函数，是因为 Lambda 函数不想方法那样属于某个特定的类。但和方法一样，Lambda 有参数列表、函数主体、返回类型，可能还有一个可以抛出异常的列表。
>
>
>
> - 传递——Lambda 表达式可以作为参数传递给方法或储存在变量中。
>
> - 简洁——无需像匿名类那样写很多模板代码。
>
> Lambda 表达式有三部分：参数列表、箭头、Lambda 主体

一个简单的例子：

``` java
Runnable runnable = () -> System.out.println("Hello World !");
runnable.run();
```

**函数描述符** 即**什么也不接受，什么也不返回（void）**，例如上面的例子。

> @FunctionalInterface 是什么
>
> 新的API中，函数式接口会带有这个。
>
> 这个注解用于表示该接口会设计成函数式接口。如果你使用@FunctionalInterface 定义一个接口，而他却不是函数式接口的话，编译器可能会返回一个错误提示`Multiple non-overriding abstract methods found in interface Foo`，表面存在多个抽象方法。/；

##### 函数式接口
| 函数式接口             | 函数描述符           | 原始类型特化                                   |
| ----------------- | --------------- | :--------------------------------------- |
| Predicate<T>      | T->boolean      | IntPredicate, LongPredicate, DoublePredicate |
| Consumer<T>       | T->void         | IntConsumer, LongConsumer, DoubleConsumer |
| Function<T,R>     | T -> R          | IntFunction<R>, IntToLongFunction, IntToDoubleFunction, LongFunction<R>, LongToIntFunction, LongToDoubleFunction, DoubleFunction<R>, ToIntFunction<T>, ToDoubleFunction<T>, ToLongFunction<T> |
| Supplier<T>       | ()->T           | BooleanSupplier, IntSupplier, LongSupplier, DoubleSupplier |
| UnaryOperator<T>  | T->T            | IntUnaryOperator, LongUnaryOperator, DoubleUnaryOperator |
| BinaryOperator<T> | (T, T)->T       | IntBinaryOperator, LongBinaryOperator, DoubleBinaryOperator |
| BiPredicate<L,R>  | (L, R)->boolean |                                          |
| BiConsumer<T,U>   | (T, U)->void    | ObjIntConsumer<T>, ObjLongConsumer<T>, ObjDoubleConsumer<T> |
| BiFunction<T,U,R> | (T, U)->R       | ToIntBiFunction<T,U>, ToLongBiFunction<T, U>, ToDoubleBiFunction<T, U> |



**类型推断** Java 编译器会从上下文（目标类型）推断出用什么函数式接口来配合Lambda。Java 7 引入的菱形运算符（<>）就是类型推断。例如：

``` java
List<Apple> inventory = new ArrayList<>();

List<Apple> greenApples = filter( inventory, a-> "green".equals(a.getColor()) ); //参数a没有显式类型，Java编译器会推断出 Apple a

Comparator<Apple> c = (Apple a1, Apple a2) -> a1.getWeight().compareTo(a2.getWeight()); //没有类型推断

Comparator<Apple> c = (a1, a2) -> a1.getWeight().compareTo(a2.getWeight()); //有类型推段
```

**注意 有时显式更易读，有时隐式更易读，需要自己判断**

**闭包 闭包就是将函数内部和函数外部连接起来的一座桥梁**。

Lambda 可以用闭包，但是，他们不能修改定义 Lambda 的方法的局部变量的内容。因为**局部变量保存在栈上**,并且隐式表示它们仅限于其所在线程。如果允许捕获可改变的局部变量，就会引发线程不安全的新可能性，但是**实例变量**可以，因为它是**保存在堆中**的，而堆实在线程之间共享的。

**方法引用**

``` java
inventory.sort( (a1, a2) -> a1.getWeight().compareTo(a2.getWeight()) );
//使用方法引用和java.util.Comparator.comparing后
inventory.sort(comparing(Apple::getWeight));
```

> 方法引用主要有三类
>
> 1. 指向***静态方法***的方法引用
> 2. 指向***任意类型实例方法***的方法引用
> 3. 指向***现有对象的实例方法***的方法引用

**构造函数引用**

``` java
Supplier<Apple> c1 = Apple::new;
Apple a1 = c1.get();
//等价于
Supplier<Apple> c1 = new Apple();
Apple a1 = c1.get();
```

结合前面的知识实践操作一下

[苹果排序 Sorting.java](src/main/ava/chapter3/Sorting.java)

**复合 Lambda 表达式的有用方法**

1. 比较器复合

``` java
inventory.sort(comparing(Apple::getWeight)
               .reversed() // 递减排序
               .thenComparing(Apple::getCountry)); // 两个苹果一样重时，比较国家
```

2. 谓词复合

``` java
Predicate<Apple> redAndHeavyAppleOrGreen = redApple.and(a -> a.getWeight() > 150)
  											    .or(a -> "green".equals(a.getWeight()));
```

3. 函数复合

``` java
Function<Integer, Integer> f = x -> x + 1;
Function<Integer, Integer> g = x -> x * 2;
Function<Integer, Integer> h = f.andThen(g); // 数学上写作g(f(x)) 或 (g o f)(x)
Function<Integer, Integer> i = f.compose(g); // 数学上写作f(g(x)) 或 (f o g)(x)
int resultToh = h.apply(1); // 4 == 1->f->2->g->4
int resultToi = i.apply(1); // 3 == 1->g->2->f->3
```

#### 小结

> - Lambda 表达式可以理解为一种匿名函数：他没有名称，但有参数列表、函数主体、返回类型，可能还有一个可以抛出异常的列表。
> - Lambda 表达式可以简洁的传递代码。
> - 函数式接口就是仅仅声明了一个抽象方法的接口。
> - 只有在接受函数式接口的地方才可以使用Lambda 表达式。
> - Lambda 表达式允许你直接内联，为函数式接口的抽象方法提供实现，并且***将整个表达式作为函数式接口的一个实例***。
> - Java 8 自带一些常用的函数式接口，如[函数式接口表](#函数式接口)。
> - 为了避免**装箱**操作，对 Predicate<T> 和 Function<T, R> 等函数式接口的原始类型特化：IntPredicate、IntToLongFunction等。
> - **环绕执行模式**（即在方法所必需的代码中间，你需要执行点儿什么操作，比如资源分配和清理）可以配合 Lambda 提高灵活性和可重用性。
> - Lambda 表达式所需要代表的类型称为**目标类型。**
> - 方法引用让你重复使用现有的方法实现并直接传递它们。
> - Comparator、Predicate、Function 等函数式接口都有几个可以用来结合 Lambda 表达式的默认方法。

## 第二部分 函数式数据处理

### 4. 引入流

#### 小结

> - 流是 “从支持数据处理操作的源生成的一系列元素”。
> - 流利用内部迭代：迭代通过`filter`、`map`、`sorted`等操作被抽象掉了。
> - 流操作有两类：中间操作和终端操作。
> - filter 和 map 等中间操作会返回一个流，并可以链接在一起。可以用它们来设置一条流水线，但并不会生成任何结果。
> - forEach 和 count 等终端操作会返回一个非流的值，并处理流水线以返回结果。
> - 流中的元素是按需计算的。

### 5. 使用流

##### 筛选和切片

`filter(Lambda)` 根据Lambda筛选出相应的数据

``` java
List<Integer> numbers = Arrays.asList(1, 2, 3, 1, 3, 3, 2, 4);
numbers.stream()
  	   .filter(i -> i % 2 == 0)
  	   .forEach(System.out::println);
// 筛选出数组的偶数
// filter(i -> i % 2 == 0)
// 2 2 4
```

`distinct()` 过滤重复

``` java
List<Integer> numbers = Arrays.asList(1, 2, 3, 1, 3, 3, 2, 4);
numbers.stream()
  	   .filter(i -> i % 2 == 0)
  	   .distinct()
  	   .forEach(System.out::println);
// 筛选出数组的偶数,去除重复
// filter(i -> i % 2 == 0).distinct()
// 2 4
```

`limit(int n)` 获取前 n 个数据

``` java
List<Integer> numbers = Arrays.asList(1, 2, 3, 1, 3, 3, 2, 4);
numbers.stream()
  	   .limit(3)
  	   .forEach(System.out::println);
// 打印出前3个数
// limit(3)
// 1 2 3
```

`skip(int n)` 去除前 n 个数据，**limit 和 skip 是互补的**

``` java
List<Integer> numbers = Arrays.asList(1, 2, 3, 1, 3, 3, 2, 4);
numbers.stream()
  	   .skip(5)
  	   .forEach(System.out::println);
// 打印出后3个数
// skip(5)
// 3 2 4
```

##### 映射

`map(Lambda)` 接受一个函数作为参数。这个函数会被应用到每个元素上，并将其映射成一个新的元素（**映射**一词，是因为它和**转换**相似，但其中的差别在于它是“**创建一个新版本**”而不是去“**修改**”）。

``` java
String[] words = {"Hello", "World"};
Arrays.stream(words)
      .map(String::length)
      .forEach(System.out::println);
// 输出每个单词的长度
// map(String::length)
// 5 5
```

`flatMap(Lambda)` 和map作用相似，不过，他是把一个流中的每个值都换成**各自的流**，然后把所有的流**合并**成一个流。

如下面这个例子，将单词表中所有**不同的字母**打印出来，用map就实现不了：

``` java
String[] words = {"Hello", "World"};
Arrays.stream(words)
      .map(s -> s.split("")) // 将每个单词转换成其字母构成的数组：{['H', "e", "l", "l", "o"], [ "W", "o", "r", "l", "d"]}
  	  .flatMap(Arrays::stream) // 将各个流合并为一个流：{'H', "e", "l", "l", "o", "W", "o", "r", "l", "d"}
  	  .distinct() // 去除重复
      .forEach(System.out::println);
// H e l o W r d
```

##### 查找和匹配

`anyMatch` 流中是否有**一个元素**能匹配给定的谓词

``` java
Boolean[] arrays = {true, true, false};
if(Arrays.stream(arrays).anyMatch(Boolean::booleanValue)) {
  System.out.println("有一个以上匹配");
} else {
  System.out.println("没有一个匹配");
}
// 有一个以上匹配
```

`allMathc` 流中**所有元素**是否都能匹配给定的谓词

``` java
Boolean[] arrays = {true, true, false};
if(Arrays.stream(arrays).allMatch(Boolean::booleanValue)) {
  System.out.println("全部都能匹配");
} else {
  System.out.println("有一个以上不匹配");
}
// 有一个以上不匹配
```

`noneMatch` 流中**不能有任何元素**能匹配给定的谓词

``` java
Boolean[] arrays = {false, false, false};
if(Arrays.stream(arrays).noneMatch(Boolean::booleanValue)) {
  System.out.println("没有一个匹配");
} else {
  System.out.println("有一个以上匹配");
}
// 没有一个匹配
```

`findFirst` 查找流中的**第一个元素**

``` java
int[] arrays = {1, 2, 3, 4, 5};
System.out.println(Arrays.stream(arrays).findFirst().getAsInt());
// 1
```

`findAny` 返回**任意元素（一个）** ，一般配个filter使用

``` java
 int[] arrays = {1, 2, 3, 4, 5, 6};
System.out.println(Arrays.stream(arrays)
                   .map(x -> x * x)
                   .filter(x -> x % 3 == 0)
                   .findAny()
                   .getAsInt()
                  );
// 9
```

##### 归约

`reduce(T, BinaryOPerator<T>)` 将流中所有的元素迭代合并成一个结果，接受两个参数，第一个初始值，第二个将两个元素结合起来产生新值的函数。

``` java
int[] arrays = {1, 2, 3, 4, 5, 6};
System.out.println(Arrays.stream(arrays)
                   .reduce(Integer::sum)
                   .getAsInt());
// reduce((a, b) -> a + b)
// 求和 21

int[] arrays = {1, 2, 3, 4, 5, 6};
System.out.println(Arrays.stream(arrays)
                   .reduce(Integer::max)
                   .getAsInt());
// reduce(Integer::max)
// 最大值 6

int[] arrays = {1, 2, 3, 4, 5, 6};
System.out.println(Arrays.stream(arrays)
                   .reduce(Integer::min)
                   .getAsInt());
// reduce(Integer::min)
// 最小值 1
```

##### 常用流操作

| 操作        | 类型         | 返回类型        | 使用的类型/函数式接口            | 函数描述符          |
| --------- | ---------- | ----------- | ---------------------- | -------------- |
| filter    | 中间         | Stream<T>   | Predicate<T>           | T -> boolean   |
| distinct  | 中间（有状态-无界） | Stream<T>   |                        |                |
| skip      | 中间（有状态-有界） | Stream<T>   | long                   |                |
| limit     | 中间（有状态-有界） | Stream<T>   | long                   |                |
| map       | 中间         | Stream<R>   | Function<T, R>         | T -> R         |
| flatMap   | 中间         | Stream<R>   | Function<T, Stream<R>> | T -> Stream<R> |
| sorted    | 中间（有状态-无界） | Stream<T>   | Comparator<T>          | (T, T) -> int  |
| anyMatch  | 终端         | boolean     | Predicate<T>           | T -> boolean   |
| noneMatch | 终端         | boolean     | Predicate<T>           | T -> boolean   |
| allMatch  | 终端         | boolean     | Predicate<T>           | T -> boolean   |
| findAny   | 终端         | Optional<T> |                        |                |
| findFirst | 终端         | Optional<T> |                        |                |
| forEach   | 终端         | void        | Consumer<T>            | T -> void      |
| collect   | 终端         | R           | Collector<T, A, R>     |                |
| reduce    | 终端（有状态-有界） | Optional<T> | BinaryOperator<T>      | (T, T) -> T    |
| count     | 终端         | long        |                        |                |

##### 数值流、构建流、无限流

数值流：IntStream、DoubleStream、LongStream。例子见[PythagoreanTriple.java](src/main/java/chapter5/PythagoreanTriple.java) 勾股数。

构建流：流不仅可以从集合创建，也可以从值、数组、文件、以及 iterate 与 generate 等特定方法创建。例子[BuildingStreams.java](src/main/java/chapter5/BuildingStreams.java) 从六种方式构建流。

无限流：顾名思义是**无限**的，使用是必须用配合 **limit()** 截断；有Stream.iterate 和 Stream.generate 两种方法。

#### 小结

> - Stream API 可以表达复杂的数据处理查询。见表[常用流操作](#常用流操作)。
> - 可以用filter、 distinct、skip和 limit 对流做筛选和切片。
> - 可以用map和flatMap提取或转换流中的元素。
> - 可以用 findFirst 和 findAny 方法查找流中的元素。可以用 allMatch、noneMatch、anyMatch方法让流匹配给定的谓词。
> - findFirst、findAny、allMatch、noneMatch、anyMatch 这些方法都利用了短路：**找到结果就停止计算；没有必要处理整个流**。
> - 可以用 reduce方法将流中所有的元素迭代合并成一个结果，例如求和或查找最大/最小元素。
> - filter 和 map 等操作是**无状态**的，它们并不储存任何状态。reduce 等操作要存储状态才能计算出一个值。sorted 和 distinct 等操作也要存储状态，因为它们需要把流中的所有元素缓存起来才能返回一个新的流。这种操作为**有状态操作**。
> - 流有三种基本的原始类型特化：IntStream、DoubleStream、LongStream。它们的操作也有相应的特化。
> - 流不仅可以从集合创建，也可以从值、数组、文件、以及 iterate 与 generate 等特定方法创建。
> - 无限流是没有固定大小的流。

### 6. 用流收集数据

### 7. 并行数据处理与性能

## 第三部分 高效Java 8编程

### 8. 重构、测试和调试

### 9. 默认方法

### 10. 用 Optional 取代 null

### 11. CompletableFuture: 组合式异步编程

### 12. 新的日期和时间 API

## 第四部分 超越Java 8

### 13. 函数式的思考

### 14. 函数式编程的技巧

### 15. 面向对象和函数式编程的混合: Java 8 和 Scala 的比较

### 16. 结论以及 Java 的未来