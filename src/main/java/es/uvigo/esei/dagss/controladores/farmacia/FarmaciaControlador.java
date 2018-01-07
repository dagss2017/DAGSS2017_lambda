/*
 Proyecto Java EE, DAGSS-2013
 */
package es.uvigo.esei.dagss.controladores.farmacia;

import es.uvigo.esei.dagss.controladores.autenticacion.AutenticacionControlador;
import es.uvigo.esei.dagss.dominio.daos.FarmaciaDAO;
import es.uvigo.esei.dagss.dominio.daos.PacienteDAO;
import es.uvigo.esei.dagss.dominio.daos.PrescripcionDAO;
import es.uvigo.esei.dagss.dominio.daos.RecetaDAO;
import es.uvigo.esei.dagss.dominio.entidades.*;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

/**
 *
 * @author ribadas
 */
@Named(value = "farmaciaControlador")
@SessionScoped
public class FarmaciaControlador implements Serializable {

    private Farmacia farmaciaActual;
    private List<Prescripcion> prescripciones;
    private List<Receta> recetas;
    private String nif;
    private String password;
    private String tarjetaSanitaria = "";
    private Paciente paciente = null;
    @Inject
    private AutenticacionControlador autenticacionControlador;

    @EJB
    private FarmaciaDAO farmaciaDAO;
    
    @EJB
    private PrescripcionDAO prescripcionDAO;
    
    @EJB
    private PacienteDAO pacienteDAO;
    
    @EJB
    private RecetaDAO recetaDAO;

    /**
     * Creates a new instance of AdministradorControlador
     */
    public FarmaciaControlador() {
    }
    
    public Paciente getPaciente(){
        return this.paciente;
    }
    
    public List<Prescripcion> getPrescripciones(){
        return this.prescripciones;
    }
    
    public String getNif() {
        return nif;
    }

    public void setNif(String nif) {
        this.nif = nif;
    }

    public String getPassword() {
        return password;
    }
    
    public List<Receta> getReceta(){
        return this.recetas;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Farmacia getFarmaciaActual() {
        return farmaciaActual;
    }

    public String getTarjetaSanitaria() {
        return this.tarjetaSanitaria;
    }

    public void setTarjetaSanitaria(String tarjetaSanitaria) {
        this.tarjetaSanitaria = tarjetaSanitaria;
    }

    public void setFarmaciaActual(Farmacia farmaciaActual) {
        this.farmaciaActual = farmaciaActual;
    }

    private boolean parametrosAccesoInvalidos() {
        return ((nif == null) || (password == null));
    }

    public String doLogin() {
        String destino = null;
        if (parametrosAccesoInvalidos()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "No se ha indicado un nif o una contraseña", ""));
        } else {
            Farmacia farmacia = farmaciaDAO.buscarPorNIF(nif);
            if (farmacia == null) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "No existe una farmacia con el NIF " + nif, ""));
            } else {
                if (autenticacionControlador.autenticarUsuario(farmacia.getId(), password,
                        TipoUsuario.FARMACIA.getEtiqueta().toLowerCase())) {
                    farmaciaActual = farmacia;
                    destino = "privado/index";
                } else {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Credenciales de acceso incorrectas", ""));
                }
            }
        }
        return destino;
    }

    public String buscarPaciente() {
        this.paciente = pacienteDAO.buscarPorTarjetaSanitaria(this.tarjetaSanitaria);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/M/dd");
        String fecha = sdf.format(new Date());
        try {
            Date a =sdf.parse(fecha);
            this.prescripciones=prescripcionDAO.buscarPrescripcionesPacienteFecha(paciente.getDni(),a);
            /*for(Prescripcion p: this.prescripciones){
                for(Receta r:p.getRecetas()){
                    p.setEstado(prescripcionDAO.obtenerEstadoReceta(r.getId()));
                }
            }*/
        } catch (ParseException ex) {
            Logger.getLogger(FarmaciaControlador.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "buscarPaciente";
    }
    
    public String doShowReceta(List<Receta> recetas){
        this.recetas=recetas;
        return "detalleReceta";
    }
    
    public void doActualizarReceta(Receta receta){
        recetaDAO.actualizar(receta);
    }
    
    public String recetas(){
        return "listaRecetas";
    }

}
