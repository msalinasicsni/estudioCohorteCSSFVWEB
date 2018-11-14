package ni.com.sts.estudioCohorteCssfv.controller.seguridad;

import java.util.Map;

import ni.com.sts.estudioCohorteCssfv.datos.seguridad.SeguridadDA;
import ni.com.sts.estudioCohorteCssfv.servicios.SeguridadService;
import ni.com.sts.estudioCohorteCssfv.util.Utilidades;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.util.Initiator;
import org.zkoss.zul.Include;

import ni.com.sts.seguridadWS.webservices.InfoSesionDTO;

/**
 * Esta clase se ejecutar� al momento de la inicializaci�n de aquellas p�ginas que la apliquen,
 * a fin de verificar si el usuario est� autenticado, o bien si tiene permisos a las opciones
 * de men� seg�n los roles asociados de forma directa o a trav�s de los grupos a los que pertenece.
 * <p>
 *
 * @author STS
 * @version 1.0, &nbsp; 15/02/2015
 * @since jdk1.6
*/
public class InitController implements Initiator {

    private static final SeguridadService seguridadService = new SeguridadDA();

    @Override
    public void doInit(Page page, Map<String, Object> args) throws Exception {

        //Si el usuario no cuenta con una sesi�n de trabajo, entonces
        //es redirigio a la p�gina de autenticaci�n de usuarios.
        InfoSesionDTO  infoSesion = Utilidades.obtenerInfoSesion();
        if(infoSesion==null){
             Executions.sendRedirect("/login.zul");
             return;
        }

        Include include = (Include)Selectors.iterable(page, "[id$=mainInclude]").iterator().next();
        String url = include.getSrc();

        if(!url.contains("inicio")){
            //Si el usuario intenta acceder a una p�gina a lo que no est� autorizado,
            //su sesi�n es eliminada para luego ser redirigido a la p�gina de autenticaci�n.
            if(seguridadService.esUsuarioAutorizado(infoSesion.getUsuarioId(),
                    Utilidades.getCodigoSistema(), url)==false){
                Utilidades.eliminarSesion();
                Executions.sendRedirect("/login.zul");
            }
        }
    }

}
