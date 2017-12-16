/*
 Proyecto Java EE, DAGSS-2013
 */
package es.uvigo.esei.dagss.controladores.medico;

import es.uvigo.esei.dagss.controladores.autenticacion.AutenticacionControlador;
import es.uvigo.esei.dagss.dominio.daos.MedicoDAO;
import es.uvigo.esei.dagss.dominio.entidades.Medico;
import es.uvigo.esei.dagss.dominio.entidades.TipoUsuario;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import java.util.List;
import es.uvigo.esei.dagss.dominio.entidades.Cita;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;


/**
 *
 * @author ribadas
 */

@Named(value = "medicoControlador")
@SessionScoped
public class MedicoControlador implements Serializable {

    private Medico medicoActual;
    private String dni;
    private String numeroColegiado;
    private String password;
    private String nombre;
    private String apellidos;
    
    private List<Cita> citas;
    private Cita citaDetalle;

    @Inject
    private AutenticacionControlador autenticacionControlador;
    

    @EJB
    private MedicoDAO medicoDAO;
    /**
     * Creates a new instance of AdministradorControlador
     */
    public MedicoControlador() {
    }
    
    public String getNombre(){
        return nombre;
    }
    
    public void setNombre(String nombre){
        this.nombre=nombre;
    }
    
    public String getApellidos(){
        return apellidos;
    }
    
    public void setApellidos(String apellidos){
        this.apellidos=apellidos;
    }
    
    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getNumeroColegiado() {
        return numeroColegiado;
    }

    public void setNumeroColegiado(String numeroColegiado) {
        this.numeroColegiado = numeroColegiado;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Medico getMedicoActual() {
        return medicoActual;
    }

    public void setMedicoActual(Medico medicoActual) {
        this.medicoActual = medicoActual;
    }
    
    public List<Cita> getCitas(){
        return citas;
    }
    
    public void setCitas(List<Cita> citas){
        this.citas = citas;
    }
    
     public Cita getCitaDetalle(){
        return citaDetalle;
    }
    
    public void setCitaDetalle(Cita citaDetalle){
        this.citaDetalle=citaDetalle;
    }

    private boolean parametrosAccesoInvalidos() {
        return (((dni == null) && (numeroColegiado == null)) || (password == null));
    }

    private Medico recuperarDatosMedico() {
        Medico medico = null;
        if (dni != null) {
            medico = medicoDAO.buscarPorDNI(dni);
        }
        if ((medico == null) && (numeroColegiado != null)) {
            medico = medicoDAO.buscarPorNumeroColegiado(numeroColegiado);
        }
        return medico;
    }

    public String doLogin() {
        String destino = null;
        if (parametrosAccesoInvalidos()) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "No se ha indicado un DNI ó un número de colegiado o una contraseña", ""));
        } else {
            Medico medico = recuperarDatosMedico();
            if (medico == null) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "No existe ningún médico con los datos indicados", ""));
            } else {
                if (autenticacionControlador.autenticarUsuario(medico.getId(), password,
                        TipoUsuario.MEDICO.getEtiqueta().toLowerCase())) {
                    medicoActual = medico;
                    destino = "privado/index";
                } else {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Credenciales de acceso incorrectas", ""));
                }
            }
        }
        return destino;
    }
    
    public void actualizarCredenciales(){
        //Medico m = new Medico();
        //m.setNumeroColegiado(numeroColegiado);
        //m.setDni(dni);
        //m.setNombre(nombre);
        //m.setApellidos(apellidos);
        //m.setNumeroColegiado(numeroColegiado);
        //m.setPassword(password);
        //Medico(String numeroColegiado, String dni, String nombre, String apellidos, CentroSalud centroSalud, String telefono, String email) {
        medicoDAO.actualizarCredenciales(medicoActual);
    }
    
    public String doDevolverAllCitasMedico() throws ParseException{
       String fechaHoy = this.getFechaHoy();
       Date dFechaHoy = this.convertStringFecha(fechaHoy);
       
       citas = medicoDAO.buscarCitasPorMedicoDia(medicoActual.getId(), dFechaHoy);
       
       return "agenda";        
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
    public String doShowCita(Cita c) {
        citaDetalle = c;
        
        return "detallesCita";
    }
}
