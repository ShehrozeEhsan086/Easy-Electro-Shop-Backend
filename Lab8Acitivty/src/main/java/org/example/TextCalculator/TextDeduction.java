package org.example.TextCalculator;

public class TextDeduction {

    public double calculateStandardDeduction(String filingStatus){
        double standardDeduction = 0;
        switch (filingStatus) {
            case "Single":
                standardDeduction = 4750;
                break;
            case "Married Joint":
                standardDeduction = 9500;

                break;
            case "Married Separate":
                standardDeduction = 7000;
                break;
            default:
                throw new IllegalArgumentException("Invalid filing status: " + filingStatus);
        }
        return standardDeduction;


    }

    public double calculateStandardDeduction(String filingStatus, int age, boolean isBlind, boolean eitherOneIsNotTakingStandardDeduction){
        double standardDeduction = calculateStandardDeduction(filingStatus);

        if(filingStatus.equals("Married Joint")){
            if(age >= 65){
                if(isBlind){
                    standardDeduction += 1000+1000;
                }
                else {
                    standardDeduction += 1000;
                }
            } else if(isBlind){
                standardDeduction += 1000;
            }
        }
        else if(eitherOneIsNotTakingStandardDeduction && filingStatus.equals("Married Separate")){
            standardDeduction = 0;
        }

        return standardDeduction;
    }



}




