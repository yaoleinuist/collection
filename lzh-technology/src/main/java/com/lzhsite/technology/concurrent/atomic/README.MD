compareAndSet()方法就是CAS的具体操作，利用CPU的CAS指令，同时借助JNI来完成Java的非阻塞算法

此时又引出一个问题（大神博客中提到的）ABA问题：

因为CAS需要在操作值的时候检查下值有没有发生变化，如果没有发生变化则更新，但是如果一个值原来是A，变成了B，又变成了A，那么使用CAS进行检查时会发现它的值没有发生变化，但是实际上却变化了。

解决方法就是atomic包下的这两个类：AtomicStampedReference/AtomicMarkableReference

AtomicMarkableReference 类描述的一个<Object,Boolean>的对，可以原子的修改Object或者Boolean的值，这种数据结构在一些缓存或者状态描述中比较有用。这种结构在单个或者同时修改Object/Boolean的时候能够有效的提高吞吐量。 


AtomicStampedReference 类维护带有整数“标志”的对象引用，可以用原子方式对其进行更新。对比AtomicMarkableReference 类的<Object,Boolean>，AtomicStampedReference 维护的是一种类似<Object,int>的数据结构，其实就是对对象（引用）的一个并发计数（标记版本戳stamp）。但是与AtomicInteger 不同的是，此数据结构可以携带一个对象引用（Object），并且能够对此对象和计数同时进行原子操作。
