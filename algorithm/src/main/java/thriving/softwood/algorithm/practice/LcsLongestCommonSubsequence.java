package thriving.softwood.algorithm.practice;

public class LcsLongestCommonSubsequence {

    public static int func(String text1, String text2) {
        int m = text1.length();
        int n = text2.length();
        int[][] dp = new int[m + 1][n + 1];

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (text1.charAt(i - 1) == text2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                } else {
                    dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
                }
            }
        }

        return dp[m][n];
    }

    static void main() {
        IO.println(func("某虎公司是全国最大的汽车后市场服务提供商", "某虎公司主要经营轮胎、保养、汽车美容、汽车用品等业务"));
    }
}
