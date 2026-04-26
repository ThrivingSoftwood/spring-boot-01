package thriving.softwood.algorithm.practice;

import java.util.Scanner;

/**
 * BalancedMatrices Optimized
 *
 * Optimization Strategy: Replaced the naive O(N^5) summation loop with 2D Prefix Sum algorithm. - Preprocessing time:
 * O(N^2) - Query time: O(1) per submatrix - Total Complexity: O(N^3)
 *
 * @author ThrivingSoftwood
 */
public class BalancedMatrices {

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);

        // 读取 N，增加健壮性处理
        if (!in.hasNextLine()) {
            return;
        }
        int n = Integer.parseInt(in.nextLine().trim());

        // 📘 定义前缀和数组
        // prefix[i][j] 表示原矩阵中前 i 行、前 j 列形成的矩形区域的总和
        // 使用 n+1 是为了处理边界情况，避免大量的 if (i>0) 判断
        int[][] prefix = new int[n + 1][n + 1];

        int rowIdx = 0;
        while (in.hasNextLine() && rowIdx < n) {
            String rowStr = in.nextLine();
            // 如果读取空行则跳过（增强鲁棒性）
            if (rowStr.isEmpty()) {
                continue;
            }

            for (int colIdx = 0; colIdx < n; colIdx++) {
                // 解析当前值：'0' -> -1, '1' -> 1
                int val = (rowStr.charAt(colIdx) == '0') ? -1 : 1;

                // 📘 核心公式 1：构建前缀和
                // 当前位置前缀和 = 上方 + 左方 - 左上方(重复部分) + 当前值
                // 注意：prefix 下标从 1 开始，对应原矩阵下标 0
                prefix[rowIdx + 1][colIdx + 1] =
                    prefix[rowIdx][colIdx + 1] + prefix[rowIdx + 1][colIdx] - prefix[rowIdx][colIdx] + val;
            }
            rowIdx++;
        }

        // 保持原有的输出逻辑
        System.out.println(0);

        // 遍历所有可能的子矩阵大小 (从 2 到 N)
        for (int size = 2; size <= n; size++) {
            int balancedCnt = 0;

            // 遍历所有可能的左上角起点
            // r 和 c 代表原矩阵的索引
            for (int r = 0; r <= n - size; r++) {
                for (int c = 0; c <= n - size; c++) {

                    // 定义子矩阵的右下角边界 (在 prefix 数组中的坐标)
                    int r1 = r, c1 = c; // Top-Left (exclusive in logic)
                    int r2 = r + size, c2 = c + size; // Bottom-Right (inclusive)

                    // 📘 核心公式 2：O(1) 获取子矩阵和
                    // 子矩阵和 = 右下 - 左下 - 右上 + 左上
                    int subMatrixSum = prefix[r2][c2] - prefix[r1][c2] - prefix[r2][c1] + prefix[r1][c1];

                    if (subMatrixSum == 0) {
                        balancedCnt++;
                    }
                }
            }
            System.out.println(balancedCnt);
        }

        in.close();
    }
}