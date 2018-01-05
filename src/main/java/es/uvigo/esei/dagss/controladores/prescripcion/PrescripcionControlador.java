/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uvigo.esei.dagss.controladores.prescripcion;


import es.uvigo.esei.dagss.dominio.daos.MedicamentoDAO;
import es.uvigo.esei.dagss.dominio.daos.PrescripcionDAO;
import es.uvigo.esei.dagss.dominio.entidades.Medico;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import java.util.List;
import es.uvigo.esei.dagss.dominio.entidades.Cita;
import es.uvigo.esei.dagss.dominio.entidades.Medicamento;
import es.uvigo.esei.dagss.dominio.entidades.Paciente;
import es.uvigo.esei.dagss.dominio.entidades.Prescripcion;
import es.uvigo.esei.dagss.dominio.entidades.Receta;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;
import java.util.LinkedList;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author burni
 */
@Named(value = "prescripcionControlador")
@SessionScoped
public class PrescripcionControlador implements Serializable{
    
    private List<Prescripcion> prescripciones;
    private List<Medicamento> medicamentos;

    private Medicamento selectedMed;
    private Prescripcion prescripcion;
    private Cita citaDetalle;
    private Medico medicoActual;
    
    @EJB
    private MedicamentoDAO medicamentoDAO;
    @EJB
    private PrescripcionDAO prescripcionDAO;
        
    /**
     * Creates a new instance of AdministradorControlador
     */
    public PrescripcionControlador() {
    }

    public List<Medicamento> getMedicamentos() {
        return medicamentos;
    }

    public void setMedicamentos(List<Medicamento> medicamentos) {
        this.medicamentos = medicamentos;
    }
    
    public List<Prescripcion> getPrescripciones() {
        return prescripciones;
    }

    public void setPrescripciones(List<Prescripcion> prescripciones) {
        this.prescripciones = prescripciones;
    }
    
    public Prescripcion getPrescripcion(){
        return prescripcion;
    }
    
    public void setPrescripcion(Prescripcion prescripcion){
        this.prescripcion=prescripcion;
    }
    
    public void setSelectedMed(Medicamento selectedMed) {
        this.selectedMed = selectedMed;
    }
    
    public Medicamento getSelectedMed() {
        return this.selectedMed;
    }
    
    public String getFechaHoy(){
       Date date = Calendar.getInstance().getTime();
       SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
       String d = sdf.format(date);
       return d;
    }
    
    public Date convertStringFecha(String d) throws ParseException{
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = format.parse(d);
        return date;
    }
    
    //Acciones
    public String doShowRecetas(Paciente p, Medico medicoActual, Cita citaDetalle) throws ParseException{
        this.medicoActual = medicoActual;
        this.citaDetalle = citaDetalle;
        return this.doShowRecetas(p);
    }
    
    public String doShowRecetas(Paciente p) throws ParseException{
        prescripciones = prescripcionDAO.buscarPrescripcionesPaciente(p.getId(),convertStringFecha(getFechaHoy()));
        return "listaPrescripcion";
    }
    
    public String doNuevaPrescripcion(){
        prescripcion = new Prescripcion();
        setMedicamentos(medicamentoDAO.getAll());
        return "nuevaPrescripcion";
    }
    
    public String addPrescripcion() throws ParseException{
        prescripcion.setMedico(medicoActual);
        prescripcion.setPaciente(citaDetalle.getPaciente());
        prescripcion.setMedicamento(selectedMed);
        prescripcion.setRecetas(crearListaRecetas());
        prescripcionDAO.anhadirPrescripcion(prescripcion);
        return doShowRecetas(citaDetalle.getPaciente());
    }
    
    public String doBorrarPrescripcion(Prescripcion prescripcion) throws ParseException{
        prescripcionDAO.borrarPrescripcion(prescripcion);
        return this.doShowRecetas(prescripcion.getPaciente());
    }
    
    public String doEditarPrescripcion(Prescripcion prescripcion){
        setMedicamentos(medicamentoDAO.getAll());
        this.prescripcion = prescripcion;
        return "editarPrescripcion";
    }
    
    public String editarPrescripcion() throws ParseException{
        prescripcionDAO.actualizarPrescripcion(prescripcion);
        return this.doShowRecetas(prescripcion.getPaciente());
    }
    
    public List<Receta> crearListaRecetas(){
        List<Receta> toRet = new LinkedList<>();
        
        int dosisTot = prescripcion.getDosis();
        int numDosis = prescripcion.getMedicamento().getNumeroDosis();
        int cal = dosisTot/numDosis;
        
        if(cal<1){
            cal = 1;
        }
        
        int dosisReceta = dosisTot/cal;
        int aux = dosisReceta;
        
        Calendar date = Calendar.getInstance();
        date.setTime(prescripcion.getFechaInicio());
        
        for(int i=0;i<cal;i++){
            Receta receta = new Receta();
            
            receta.setPrescripcion(prescripcion);
            
            if(dosisTot>aux){
                receta.setCantidad(aux);
                dosisTot = dosisTot - aux;
            }else{
                receta.setCantidad(dosisTot);
            }    
            
            if(date.getTime().before(prescripcion.getFechaFin())){
                receta.setInicioValidez(date.getTime());
                date.add(Calendar.DATE,6);
            }else{
                receta.setInicioValidez(prescripcion.getFechaFin());
            }
            
            if(date.getTime().before(prescripcion.getFechaFin())){
                receta.setFinValidez(date.getTime());
                date.add(Calendar.DATE,1);
            }else{
                receta.setFinValidez(prescripcion.getFechaFin());
            }
            
            toRet.add(receta);
        }
        
        return toRet;
    }
    
    public String doShowListaRecetas(Prescripcion p) throws ParseException{
        prescripcion = p;
        return "listaReceta";
    }

    public void onDateSelect(SelectEvent event) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Date Selected", format.format(event.getObject())));
    }
    
}
