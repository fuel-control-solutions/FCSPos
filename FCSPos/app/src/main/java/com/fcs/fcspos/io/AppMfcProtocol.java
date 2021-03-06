package com.fcs.fcspos.io;

import android.os.SystemClock;

import com.fcs.fcspos.model.Dispenser;
import com.fcs.fcspos.model.Programming;
import com.fcs.fcspos.model.Sale;

import java.io.Serializable;


public class AppMfcProtocol implements Serializable {

    private MfcWifiCom mfcWifiCom;
    private Programming programming;
    private byte estado;
    private final String SEPARATOR=";";
    private boolean correctHose=false;
    private Dispenser dispenser;


    public AppMfcProtocol(MfcWifiCom mfcWifiCom, Dispenser dispenser){
        this.mfcWifiCom = mfcWifiCom;
        this.dispenser = dispenser;
    }

    public void machineCommunication(boolean pendingSale){
        final int OK = 1;

        String[] splitAnswer;
        System.out.println(programming.getPosition() + "pos");
        mfcWifiCom.sendRequest("estado;" + programming.getPosition());//pido estado
        if (mfcWifiCom.getAnswer() != null) {
            System.out.println("Respuesta estado: " + mfcWifiCom.getAnswer());
            splitAnswer = mfcWifiCom.getAnswer().split(SEPARATOR);
            if(splitAnswer.length>2){
                if (splitAnswer[2].equals("A")) {
                    splitAnswer[2] = "10";
                }

                if(Integer.parseInt(splitAnswer[2]) == dispenser.getCod_ESPERA()){ //ESPERA
                    System.out.println("ESTADO ESPERA");
                    estado = dispenser.getCod_ESPERA();
                }else if(Integer.parseInt(splitAnswer[2]) == dispenser.getCod_LISTO()){ //LISTO
                    System.out.println("ESTADO LISTO");
                    estado = dispenser.getCod_LISTO();
                    if(programming.getKind()!=null && !pendingSale) {//si hay venta programada, realizarla
                        mfcWifiCom.sendRequest("autorizar;" + programming.getPosition());
                        if (mfcWifiCom.getAnswer() != null) {
                            for (int i = 0; i < splitAnswer.length; i++) {
                                splitAnswer[i] = "";
                            }
                            splitAnswer = mfcWifiCom.getAnswer().split(SEPARATOR);
                            if (Integer.parseInt(splitAnswer[2]) == OK) {
                                System.out.println("SE AUTORIZO");
                            } else {
                                System.out.println("error en la autorizacion");
                            }
                        } else {
                            System.out.println("No hubo respuesta de autorizacion");
                        }
                    }else{
                        mfcWifiCom.sendRequest("manguera;" + programming.getPosition());
                        if(mfcWifiCom.getAnswer()!= null){
                            for (int i = 0; i < splitAnswer.length; i++) {
                                splitAnswer[i] = "";
                            }
                            splitAnswer = mfcWifiCom.getAnswer().split(SEPARATOR);
                            if (Integer.parseInt(splitAnswer[2]) == programming.getProduct()) {
                                System.out.println("Manguera correcta");
                                setCorrectHose(true);
                                mfcWifiCom.sendRequest("programar;"+ programming.getPosition()
                                        +";M" + programming.getProduct() + ";T" + programming.getPresetKind()
                                        + ";P" + programming.getQuantity());
                                SystemClock.sleep(140);
                                if (mfcWifiCom.getAnswer() != null) {
                                    for(int i=0; i<splitAnswer.length; i++){
                                        splitAnswer[i]="";
                                    }
                                    splitAnswer = mfcWifiCom.getAnswer().split(SEPARATOR);
                                    if (Integer.parseInt(splitAnswer[2]) == OK) {
                                        System.out.println("SE PROGRAMO");
                                    } else {
                                        System.out.println("error en la programacion");
                                    }
                                } else {
                                    System.out.println("No hubo respuesta de la programacion");
                                }
                            } else {
                                System.out.println("manguera incorrecta");
                                setCorrectHose(false);
                            }
                        }
                    }
                }else if(Integer.parseInt(splitAnswer[2]) == dispenser.getCod_AUTORIZADO()){ //AUTORIZADO
                    System.out.println("ESTADO AUTORIZADO");
                    estado = dispenser.getCod_AUTORIZADO();
                }else if(Integer.parseInt(splitAnswer[2]) == dispenser.getCod_SURTIENDO()){ //SURTIENDO
                    System.out.println("ESTADO SURTIENDO");
                    estado = dispenser.getCod_SURTIENDO();
                }else if(Integer.parseInt(splitAnswer[2]) == dispenser.getCod_VENTA()){ //VENTA
                    System.out.println("ESTADO VENTA");
                    estado = dispenser.getCod_VENTA();
                }else if(Integer.parseInt(splitAnswer[2]) == dispenser.getCod_ERROR()){ //ERROR
                    System.out.println("ESTADO ERROR");
                    estado = dispenser.getCod_ERROR();
                }

            }
        }else {
            System.out.println("NULL EN ESTADO");
        }
    }

    private double transformVolume(String volume) {
        int x =Integer.parseInt(volume);
        return Double.parseDouble((x/1000) + "." + (x%1000));
    }

    public Programming getProgramming() {
        return programming;
    }

    public void setProgramming(Programming programming) {
        this.programming = programming;
    }

    public byte getEstado() {
        return estado;
    }

    public Sale getSale() {
        mfcWifiCom.sendRequest("venta;" + programming.getPosition());
        if (mfcWifiCom.getAnswer() != null) {
            final String[] splitSale = mfcWifiCom.getAnswer().split(SEPARATOR);
            if(splitSale.length>5){
                return new Sale(Short.parseShort(splitSale[1]),
                        Short.parseShort(splitSale[2]),Short.parseShort(splitSale[3]),
                        transformVolume(splitSale[4]),Integer.parseInt(splitSale[5]),
                        Integer.parseInt(splitSale[6]) );
            }
        }
        return null;
    }

    public boolean isCorrectHose() {
        return correctHose;
    }

    private void setCorrectHose(boolean correctHose) {
        this.correctHose = correctHose;
    }
}
