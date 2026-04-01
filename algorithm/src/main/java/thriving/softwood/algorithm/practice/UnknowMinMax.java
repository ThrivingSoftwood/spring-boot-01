package thriving.softwood.algorithm.practice;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class UnknowMinMax {

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);

        // n 和 q 控制循环次数，int 通常足够 (21亿次循环早就超时了，所以不用 long)
        int n = in.nextInt();
        int q = in.nextInt();

        // ⚠️ 优化点：移除了未使用的 int[] nums 数组，节省内存

        // 使用 long 防止累加溢出
        long baseSum = 0;
        long placeholderCnt = 0;

        // 预估 Map 容量，避免扩容开销
        // 注意：HashMap 的容量参数仍需 int
        Map<String, String> map = new HashMap<>((int)(q * 1.5));

        long currNum = 0;
        for (int i = 0; i < n; i++) {
            // 读取 long 类型的输入
            if (in.hasNextLong()) {
                currNum = in.nextLong();
                if (0 == currNum) {
                    placeholderCnt++;
                } else {
                    baseSum += currNum;
                }
            }
        }

        long min = 0;
        long max = 0;
        String key = "";
        long minValue = 0;
        long maxValue = 0;
        String value = "";

        for (int i = 0; i < q; i++) {
            min = in.nextLong();
            max = in.nextLong();

            // 构建缓存 Key
            key = min + " " + max;
            value = map.get(key);

            if (null == value) {
                // 📘 核心计算
                // baseSum 是 long，乘法结果也是 long，运算安全
                minValue = baseSum + placeholderCnt * min;

                // 三元运算符逻辑保持不变
                maxValue = (min == max) ? minValue : (baseSum + placeholderCnt * max);

                value = minValue + " " + maxValue;
                map.put(key, value);
            }
            System.out.println(value);
        }

        in.close();
    }
}