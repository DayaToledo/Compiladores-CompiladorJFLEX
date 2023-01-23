/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package interpreter;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Stack;
import machineCode.Instruction;
import machineCode.MachineCode;

/**
 *
 * @author Dayana Toledo e Gabriela Tamashiro
 */
public class Interpreter {

    private final IO io;
    private final ArrayList<Instruction> codeArea;
    private final Stack<Integer> dataArea;
    private int codePointer = 0;

    public Interpreter(ArrayList<Instruction> code, IO io) {
        this.codeArea = code;
        this.dataArea = new Stack<>();
        this.io = io;
    }

    public void run() {
        while (codePointer < codeArea.size()) {
            Instruction instruction = codeArea.get(codePointer);
            boolean desvio = execute(instruction);
            if (desvio) {
                codePointer = getRealAddress(instruction.getOperate());
            } else {
                codePointer++;
            }
            if (instruction.getInstruction().equals("PARA")) {
                return;
            }
        }
    }

    public int getRealAddress(int logicalAddress) {
        for (int i = 0; i < codeArea.size(); i++) {
            int label = codeArea.get(i).getLabel();
            if (Objects.isNull(label) && label == logicalAddress) {
                return i;
            }
        }
        return codeArea.size();
    }

    private boolean execute(Instruction instruction) {
        int aux;
        switch (instruction.getInstruction()) {
            case MachineCode.INICIAR_PROGRAMA:
                dataArea.clear();
                break;
            case MachineCode.ALOCAR_MEMORIA:
                for (int i = 0; i < instruction.getOperate(); i++) {
                    dataArea.add(0);
                }
                break;
            case MachineCode.DESALOCAR_MEMORIA:
                for (int i = 0; i < instruction.getOperate(); i++) {
                    dataArea.pop();
                }
                break;
            case MachineCode.CARREGAR_CONSTANTE:
                dataArea.add(instruction.getOperate());
                break;
            case MachineCode.CARREGAR_VALOR:
                dataArea.add(dataArea.get(instruction.getOperate()));
                break;
            case MachineCode.ARMAZENAR_VALOR:
                dataArea.set(instruction.getOperate(), dataArea.pop());
                break;
            case MachineCode.OPERADOR_SOMA:
                aux = dataArea.pop();
                dataArea.add(dataArea.pop() + aux);
                break;
            case MachineCode.OPERADOR_SUB:
                aux = dataArea.pop();
                dataArea.add(dataArea.pop() - aux);
                break;
            case MachineCode.OPERADOR_MULT:
                aux = dataArea.pop();
                dataArea.add(dataArea.pop() * aux);
                break;
            case MachineCode.OPERADOR_DIV:
                aux = dataArea.pop();
                dataArea.add(dataArea.pop() / aux);
                break;
            case MachineCode.INVERTER_SINAL:
                dataArea.add(-dataArea.pop());
                break;
            case MachineCode.OPERADOR_AND:
                dataArea.add(dataArea.pop() & dataArea.pop());
                break;
            case MachineCode.OPERADOR_OR:
                dataArea.add(dataArea.pop() | dataArea.pop());
                break;
            case MachineCode.OPERADOR_NOT:
                dataArea.add(1 - dataArea.pop());
                break;
            case MachineCode.COMPARAR_IGUAL:
                aux = dataArea.pop();
                dataArea.add((((int) dataArea.pop()) == ((int) aux)) ? 1 : 0);
                break;
            case MachineCode.COMPARAR_DIFERENTE:
                aux = dataArea.pop();
                dataArea.add((((int) dataArea.pop()) != ((int) aux)) ? 1 : 0);
                break;
            case MachineCode.COMPARAR_MAIOR:
                aux = dataArea.pop();
                dataArea.add((((int) dataArea.pop()) > ((int) aux)) ? 1 : 0);
                break;
            case MachineCode.COMPARAR_MAIOR_IGUAL:
                aux = dataArea.pop();
                dataArea.add((((int) dataArea.pop()) >= ((int) aux)) ? 1 : 0);
                break;
            case MachineCode.COMPARAR_MENOR:
                aux = dataArea.pop();
                dataArea.add((((int) dataArea.pop()) < ((int) aux)) ? 1 : 0);
                break;
            case MachineCode.COMPARAR_MENOR_IGUAL:
                aux = dataArea.pop();
                dataArea.add((((int) dataArea.pop()) <= ((int) aux)) ? 1 : 0);
                break;
            case MachineCode.DESVIAR_SEMPRE:
                return true;
            case MachineCode.DESVIAR_SE_FALSE:
                return dataArea.pop() == 0;
            case MachineCode.NADA:
                break;
            case MachineCode.OPERACAO_LEITURA:
                dataArea.add(io.readInt());
                break;
            case MachineCode.OPERACAO_IMPRIME:
                io.printInt(dataArea.pop());
                break;
        }
        return false;
    }
}
