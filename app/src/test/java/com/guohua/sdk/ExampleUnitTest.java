package com.guohua.sdk;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
        byte[] sendDatas = new byte[]{0x7C, 111, 0, 0, 10, 0, 0, 121};
        String call_reminder_shinemode_value = new String(sendDatas);
        byte[] getBytes = call_reminder_shinemode_value.getBytes();
        for (int i = 0; i < getBytes.length; i++) {
            System.out.print(getBytes[i] + "; ");
        }

        String[] s = ("124;111;0;0;10;0;0;121;").split(";");
        int[] sendIntDatas = new int[s.length];
        System.out.println("\nsendIntDatas  -------------------------------");
        for (int i = 0; i < s.length; i++) {
            sendIntDatas[i] = Integer.parseInt(s[i]);
            System.out.print(sendIntDatas[i] + ", ");
        }
        System.out.println("\nsendIntDatas  ===============================");
    }
}