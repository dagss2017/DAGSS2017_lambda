/*
 Proyecto Java EE, DAGSS-2013
 */
package es.uvigo.esei.dagss.controladores.medico;


import es.uvigo.esei.dagss.controladores.autenticacion.AutenticacionControlador;
import es.uvigo.esei.dagss.dominio.daos.CitaDAO;
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
import org.primefaces.event.SelectEvent;


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
    
    private List<Cita> citas;
    private Cita citaDetalle;

    @Inject
    private AutenticacionControlador autenticacionControlador;
    

    @EJB
    private MedicoDAO medicoDAO;
    @EJB
    private CitaDAO citaDAO;
    
        /**
     * Creates a new instance of AdministradorControlador
     */
    public MedicoControlador() {
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
        medicoDAO.actualizarCredenciales(medicoActual);
    }
    
    public String doDevolverAllCitasMedico() throws ParseException{
       String fechaHoy = this.getFechaHoy();
       Date dFechaHoy = this.convertStringFecha(fechaHoy);
       
       citas = citaDAO.buscarCitasPorMedicoDia(medicoActual.getId(), dFechaHoy);
       
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
    
    public String doActualizarEstadoCita() throws ParseException{
        citaDAO.actualizarEstadoCita(citaDetalle);
        return this.doDevolverAllCitasMedico();
    }
    
    public void onDateSelect(SelectEvent event) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Date Selected", format.format(event.getObject())));
    }
    
    public String detalleMedico(Medico medico){
        this.medicoActual=medico;
        return "./detalleMedico.xhtml";
    }
}
