/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package machineCode;

import java.util.ArrayList;

/**
 *
 * @author Dayana Toledo e Gabriela Tamashiro
 */
public class MachineCode {

    public static final String INICIAR_PROGRAMA = "INPP";
    public static final String ALOCAR_MEMORIA = "AMEM";
    public static final String DESALOCAR_MEMORIA = "DMEM";
    public static final String CARREGAR_VALOR = "CRVL";
    public static final String CARREGAR_CONSTANTE = "CRCT";
    public static final String ARMAZENAR_VALOR = "ARMZ";
    public static final String PARAR = "PARA";
    public static final String NADA = "NADA";
    public static final String OPERACAO_IMPRIME = "IMPR";
    public static final String OPERACAO_LEITURA = "LEIT";
    public static final String OPERADOR_NOT = "NEGA";
    public static final String OPERADOR_MULT = "MULT";
    public static final String OPERADOR_DIV = "DIVI";
    public static final String OPERADOR_AND = "CONJ";
    public static final String OPERADOR_OR = "DISJ";
    public static final String OPERADOR_SOMA = "SOMA";
    public static final String OPERADOR_SUB = "SUBT";
    public static final String OPERADOR_MULTOR = "DISJ";
    public static final String COMPARAR_MENOR = "CMME";
    public static final String COMPARAR_MAIOR = "CMMA";
    public static final String COMPARAR_IGUAL = "CMIG";
    public static final String COMPARAR_DIFERENTE = "CMDG";
    public static final String COMPARAR_MAIOR_IGUAL = "CMAG";
    public static final String COMPARAR_MENOR_IGUAL = "CMEG";
    public static final String INVERTER_SINAL = "INVR";
    public static final String DESVIAR_SE_FALSE = "DSVF";
    public static final String DESVIAR_SEMPRE = "DSVS";

    private final ArrayList<Instruction> instructions;

    public MachineCode() {
        instructions = new ArrayList<>();
    }

    public static ArrayList<Instruction> getProgramfromString(String text) {
        ArrayList<Instruction> codeLALG = new ArrayList<>();
        String[] rows = text.split("\n");

        for (String row : rows) {
            String[] tokens = row.trim().split("(\\s+)|(\\t+)");
            for (String token : tokens) {
                token = token.trim();
            }
            codeLALG.add(getInstructionsFromTokens(tokens));
        }
        return codeLALG;
    }

    private static Instruction getInstructionsFromTokens(String[] tokens) {
        Integer label = null;
        Integer operate = null;
        String instruction = null;

        switch (tokens.length) {
            case 1:
                instruction = tokens[0];
                break;
            case 2:
                if (tokens[0].matches("\\d+")) {
                    label = 0;
                    instruction = tokens[1];
                } else {
                    operate = 0;
                    instruction = tokens[0];
                }
                break;
            case 3:
                label = Integer.valueOf(tokens[0]);
                operate = Integer.valueOf(tokens[2]);
                instruction = tokens[1];
                break;
            default:
                break;
        }
        return new Instruction(label, instruction, operate);
    }

    public void addInstruction(String instruction, Integer operate) {
        Integer label = this.getSize();
        this.instructions.add(new Instruction(label, instruction, operate));
    }

    public void updateInstruction(Integer label, String instruction, Integer operate) {
        this.instructions.set(label, new Instruction(label, instruction, operate));
    }

    public Integer getSize() {
        return instructions.size();
    }

    public ArrayList<Instruction> getInstructions() {
        return instructions;
    }

    public void clear() {
        instructions.clear();
    }

    @Override
    public String toString() {
        Integer size = this.getSize();
        StringBuilder instructionsStr = new StringBuilder(size * 7);
        for (Instruction instruction : instructions) {
            instructionsStr.append(instruction.toString());
            instructionsStr.append("\n");
        }
        return instructionsStr.toString();
    }
}
