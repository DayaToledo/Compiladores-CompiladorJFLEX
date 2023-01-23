/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package machineCode;

/**
 *
 * @author Dayana Toledo e Gabriela Tamashiro
 */
public class Instruction {

    private Integer label;
    private String instruction;
    private Integer operate;

    public Instruction(Integer label, String instruction, Integer operate) {
        this.label = label;
        this.instruction = instruction;
        this.operate = operate;
    }

    public String getLabelStr() {
        if (label == null) {
            return "";
        }
        return String.valueOf(label);
    }

    public int getLabel() {
        return label;
    }

    public void setLabel(Integer label) {
        this.label = label;
    }

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    public String getOperateStr() {
        if (operate == null) {
            return "";
        }
        return String.valueOf(operate);
    }

    public int getOperate() {
        return operate;
    }

    public void setOperate(Integer operate) {
        this.operate = operate;
    }
}
