package Controllers;

import Models.Modelo;
import Support.Routines;
import Views.viewProcesar;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.event.*;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProcesarController implements ActionListener, KeyListener, FocusListener {

    private Modelo model;
    private viewProcesar view;
    private Routines rut;
    private Border original;

    public ProcesarController(viewProcesar view, Modelo model){
        this.model = model;
        this.view = view;
        rut = new Routines();
        hazEscuchadores();
        original = view.getTf_idCria().getBorder();
    }

    private void hazEscuchadores(){
        view.getTf_idCria().addFocusListener(this);
        view.getTf_idCria().addKeyListener(this);

        view.getBtn_Buscar().addActionListener(this);
        view.getBtn_procesar().addActionListener(this);
    }


    @Override
    public void actionPerformed(ActionEvent evt) {

        JButton button = (JButton)evt.getSource();

        if(view.getTf_idCria().getText().isEmpty()) {
            rut.msgError("El ID de cría está vacío");
            return;
        }

        if(button == view.getBtn_Buscar()){
            onClicBuscar();
        }
        if(button == view.getBtn_procesar()){
            if(view.getDp_fechaS().getText().isEmpty()){
                rut.msgError("Seleccione una fecha válida");
                return;
            }
            onClicProcesar();
        }
    }

    private void onClicProcesar(){
        int idCria = Integer.parseInt(view.getTf_idCria().getText());
        try{
            String fechaS = view.getDp_fechaS().getDate().toString();
            System.out.println(fechaS);
            model.sp_updateCriasFechaS(idCria,fechaS);
        }catch (SQLException e){
            rut.msgError("La fecha de salida no puede ser menor que la fecha de llegada de la cría");
        }

    }

    private void onClicBuscar(){
        ResultSet rs;
        int idCria = Integer.parseInt(view.getTf_idCria().getText());

        try{
            rs = model.sp_selectCriasClasificadas(idCria);
            if(rs.next()){
                int corralNo = rs.getInt("corral_no");
                String cria_fechaL = rs.getString("cria_fechaL");
                String cria_salud = rs.getString("cria_salud");
                int clas_grasaCob = rs.getInt("clas_grasCobertura");

                view.getTf_fechaL().setText(cria_fechaL);
                view.getTf_noCorral().setText(corralNo+"");
                view.getTf_salud().setText(rut.salud(cria_salud));
                view.getTf_grasaCob().setText(clas_grasaCob+"");
            }
            else
                rut.msgError("Este ID no ha sido registrado o es repetido");
        }catch(SQLException e){
            rut.msgError(e.getMessage());
        }


    }

    @Override
    public void focusGained(FocusEvent evt) {
        JTextField aux = (JTextField) evt.getSource();
        aux.selectAll();
    }

    @Override
    public void focusLost(FocusEvent evt) {
        JTextField aux = (JTextField) evt.getSource();
        rut.borderCheck(aux, original);

    }

    @Override
    public void keyTyped(KeyEvent evt) {
        JTextField aux = (JTextField) evt.getSource();
        rut.soundAlert(evt, aux, 10);
        rut.onlyNumbers(evt,aux);
    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
