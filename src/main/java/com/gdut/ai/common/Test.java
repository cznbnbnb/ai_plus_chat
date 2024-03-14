package com.gdut.ai.common;

import java.util.Scanner;

public class Test {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt(); // 读取基地数量
        int[] h = new int[n]; // 储存每个基地的小兵数量
        for (int i = 0; i < n; i++) {
            h[i] = scanner.nextInt(); // 读取每个基地的小兵数量
        }
        scanner.close();

        int minEnergy = 0; // 初始化所需的最小能量值
        int currentEnergy = 0; // 初始化当前能量值

        // 从最后一个基地向第一个基地迭代
        for (int i = n - 1; i >= 0; i--) {
            int energyNeeded = h[i] - currentEnergy;
            if (energyNeeded > 0) {
                // 如果当前基地的小兵数量大于当前的能量值
                // 增加所需的最小起始能量，以确保能量不会变成负数
                minEnergy += energyNeeded;
                currentEnergy = h[i];
            }
            // 如果小兵数量小于等于当前能量值，不需要调整起始能量值
        }

        // 输出所需的最小起始能量值
        System.out.println(minEnergy);
    }


}
