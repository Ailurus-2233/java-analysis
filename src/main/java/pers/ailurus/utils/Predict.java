package pers.ailurus.utils;

import cn.hutool.core.lang.Console;
import pers.ailurus.models.feature.FeatureClass;
import pers.ailurus.models.feature.FeatureMethod;
import pers.ailurus.models.feature.FeaturePackage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class Predict {

    public static List<FeaturePackage> predict(FeaturePackage fp, double thresholdP, double thresholdC, double thresholdM) {
        List<FeaturePackage> candidate = getCandidateList(fp);
//        Console.print("{}\n", candidate);
        candidate.sort((o1, o2) -> comparePackage(o1, o2, fp));
//        Console.print("{}\n", candidate);
        double[] score = new double[candidate.size()];
        int flag = 0;
        for (int i = 0; i < candidate.size(); i++) {
            double s = getPackageSimilarityScore(fp, candidate.get(i), thresholdC, thresholdM);
            if (s >= thresholdP) {
                score[i] = s;
                flag = 0;
            } else {
                flag++;
            }
            if (flag == 5) {
                break;
            }
        }
        List<FeaturePackage> ans = new ArrayList<>();
        for (int i = 0; i < score.length; i++) {
            if (score[i] != 0) {
                ans.add(candidate.get(i));
            }
        }
        return ans;
    }

    public static int comparePackage(FeaturePackage fp1, FeaturePackage fp2, FeaturePackage src) {
        int[] base1 = fp1.getBase();
        int[] base2 = fp2.getBase();
        int[] srcBase = src.getBase();
        for (int i = 0; i < 5; i++) {
            int temp1 = Math.abs(base1[i] - srcBase[i]);
            int temp2 = Math.abs(base2[i] - srcBase[i]);
            if (temp1 != temp2) {
                return temp2 - temp1;
            }
        }
        return 0;
    }

    public static List<FeaturePackage> getCandidateList(FeaturePackage fp) {
        List<FeaturePackage> candidateList = Database.findAll("cdg_level3", fp.getCdg().getLevel3Finger());
        if (candidateList.size() == 0) {
            candidateList = Database.findAll("cdg_level2", fp.getCdg().getLevel2Finger());
            if (candidateList.size() == 0) {
                candidateList = Database.findAll("cdg_level1", fp.getCdg().getLevel1Finger());
            }
        }
        return candidateList;
    }

    public static double getPackageSimilarityScore(FeaturePackage src, FeaturePackage dst, double thresholdC, double thresholdM) {
        FeatureClass[] srcClasses = src.getClasses();
        FeatureClass[] dstClasses = dst.getClasses();

        int[] target = new int[dstClasses.length];
        double sumSimilarityScore = 0;
        for (FeatureClass srcClass : srcClasses) {
            for (int index = 0; index < dstClasses.length; index++) {
                // 找到第一个没有映射的类
                int j = index;
                while (target[j] == 1) {
                    j++;
                    if (j == dstClasses.length) {
                        break;
                    }
                }
                if (j == dstClasses.length) {
                    break;
                }
                double css = getClassSimilarityScore(srcClass, dstClasses[j], thresholdM);
                if (css >= thresholdC) {
                    sumSimilarityScore += css;
                    target[j] = 1;
                    break;
                } else
                    index = j;
            }
        }
        return sumSimilarityScore / srcClasses.length;
    }

    public static int compareMethod(FeatureMethod m1, FeatureMethod m2) {
        int[] base1 = m1.getBase();
        int[] base2 = m2.getBase();
        if (base1[0] != base2[0]) {
            return base1[0] - base2[0];
        } else {
            return base1[1] - base2[1];
        }
    }

    public static double getClassSimilarityScore(FeatureClass src, FeatureClass dst, double threshold) {
        double baseSimilarityScore = getCosine(src.getBase(), src.getBase()) * 0.2 +
                levenshteinRatio(src.getField(), dst.getField()) * 0.4;
        FeatureMethod[] srcMethods = src.getMethods();
        Arrays.sort(srcMethods, Predict::compareMethod);
        FeatureMethod[] dstMethods = dst.getMethods();
        Arrays.sort(dstMethods, Predict::compareMethod);

        double rmmt = 0;
        if (dst.getMethods().length != 0) {
            double sumSimilarityScore = 0;
            int[] target = new int[dstMethods.length];

            for (FeatureMethod srcMethod : srcMethods) {
                for (int index = 0; index < dstMethods.length; index++) {
                    // 找到第一个没有映射的方法
                    int j = index;
                    while (target[j] == 1) {
                        j++;
                        if (j == dstMethods.length)
                            break;
                    }
                    if (j == dstMethods.length) {
                        break;
                    }
                    int[] srcBase = srcMethod.getBase();
                    int[] dstBase = dstMethods[j].getBase();
                    if (srcBase[0] < dstBase[0]) {
                        break;
                    } else if (srcBase[0] == dstBase[0]) {
                        if (srcBase[1] < dstBase[1]) {
                            break;
                        }
                        if (srcBase[1] > dstBase[1]) {
                            continue;
                        }
                    } else {
                        continue;
                    }
                    double methodSimilarityScore = getMethodSimilarityScore(srcMethod, dstMethods[j]);
                    if (methodSimilarityScore >= threshold) {
                        sumSimilarityScore += methodSimilarityScore;
                        target[j] = 1;
                        break;
                    } else
                        index = j;
                }
            }
            rmmt = sumSimilarityScore / srcMethods.length;
        } else {
            rmmt = 1;
        }
        return baseSimilarityScore + rmmt * 0.4;
    }


    public static double getMethodSimilarityScore(FeatureMethod src, FeatureMethod dst) {
        return levenshteinRatio(src.getParam(), dst.getParam()) * 0.2 +
                levenshteinRatio(src.getInvoke(), dst.getInvoke()) * 0.2 +
                levenshteinRatio(src.getConstant(), dst.getConstant()) * 0.5 +
                levenshteinRatio(src.getCfg(), dst.getCfg()) * 0.1;
    }

    public static int[][] initDp(int len1, int len2) {
        int[][] dp = new int[len1 + 1][len2 + 1];
        for (int i = 0; i <= len1; i++) {
            dp[i][0] = i;
        }
        for (int i = 0; i <= len2; i++) {
            dp[0][i] = i;
        }
        return dp;
    }

    public static double levenshteinRatio(int[] src, int[] dst) {
        int len1 = src.length;
        int len2 = dst.length;
        if (len1 == 0 && len2 == 0) {
            return 1;
        }
        int[][] dp = initDp(len1, len2);
        for (int i = 1; i <= len1; i++) {
            for (int j = 1; j <= len2; j++) {
                if (src[i - 1] == dst[j - 1]) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = Math.min(dp[i - 1][j - 1], Math.min(dp[i - 1][j], dp[i][j - 1])) + 1;
                }
            }
        }
        return 1 - (double) dp[len1][len2] / Math.max(len1, len2);
    }

    public static double levenshteinRatio(String src, String dst) {
        int len1 = src.length();
        int len2 = dst.length();
        if (len1 == 0 && len2 == 0) {
            return 1;
        }
        int[][] dp = initDp(len1, len2);
        for (int i = 1; i <= len1; i++) {
            for (int j = 1; j <= len2; j++) {
                if (src.charAt(i - 1) == dst.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = Math.min(dp[i - 1][j - 1], Math.min(dp[i - 1][j], dp[i][j - 1])) + 1;
                }
            }
        }
        return 1 - (double) dp[len1][len2] / Math.max(len1, len2);
    }

    public static double getCosine(int[] src, int[] dst) {
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;
        for (int i = 0; i < src.length; i++) {
            dotProduct += src[i] * dst[i];
            normA += Math.pow(src[i], 2);
            normB += Math.pow(dst[i], 2);
        }
        if (normA == 0.0 && normB == 0.0) {
            return 1;
        } else if (normA == 0.0 || normB == 0.0) {
            return 0;
        } else {
            return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
        }
    }
}
