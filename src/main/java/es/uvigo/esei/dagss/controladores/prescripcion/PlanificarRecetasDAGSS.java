/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uvigo.esei.dagss.controladores.prescripcion;

import es.uvigo.esei.dagss.dominio.entidades.Prescripcion;
import es.uvigo.esei.dagss.dominio.entidades.Receta;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import javax.ejb.Stateless;

/**
 *
 * @author burni
 */

@Stateless
public class PlanificarRecetasDAGSS implements PlanificarRecetas{

    public PlanificarRecetasDAGSS() {
    }
    
    @Override
    public List<Receta> calcularRecetas(Prescripcion p) {
        
        List<Receta> toRet = new LinkedList<>();
        
        int dosisTot = p.getDosis();
        int numDosis = p.getMedicamento().getNumeroDosis();
        int cal = dosisTot/numDosis;
        
        if(cal<1){
            cal = 1;
        }
        
        int dosisReceta = dosisTot/cal;
        int aux = dosisReceta;
        
        Calendar date = Calendar.getInstance();
        date.setTime(p.getFechaInicio());
        
        for(int i=0;i<cal;i++){
            Receta receta = new Receta();
            
            receta.setPrescripcion(p);
            
            if(dosisTot>aux){
                receta.setCantidad(aux);
                dosisTot = dosisTot - aux;
            }else{
                receta.setCantidad(dosisTot);
            }    
            
            if(date.getTime().before(p.getFechaFin())){
                receta.setInicioValidez(date.getTime());
                date.add(Calendar.DATE,6);
            }else{
                receta.setInicioValidez(p.getFechaFin());
            }
            
            if(date.getTime().before(p.getFechaFin())){
                receta.setFinValidez(date.getTime());
                date.add(Calendar.DATE,1);
            }else{
                receta.setFinValidez(p.getFechaFin());
            }
            
            toRet.add(receta);
        }
        
        return toRet;
    }  
}
