package com.mmwwtt.java.demo.se;

public class Main {
}


class ListNode {
    int val;
    ListNode next;

    ListNode() {
    }

    ListNode(int val) {
        this.val = val;
    }

    ListNode(int val, ListNode next) {
        this.val = val;
        this.next = next;
    }
}

class Solution {
    public int[] plusOne(int[] digits) {
        digits[digits.length-1]+=1;
        for(int i = digits.length-1; i>=0; i--){
            if(digits[i]==10){
                if(i==0){
                    int[] a = new int[digits.length+1];
                    a[0]=1;
                    return a;
                }else{
                    digits[i]=0;
                    digits[i-1]+=1;
                }

            }
        }
        return digits;
    }
}