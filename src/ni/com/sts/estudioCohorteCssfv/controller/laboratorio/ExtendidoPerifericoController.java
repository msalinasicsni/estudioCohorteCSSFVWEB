package ni.com.sts.estudioCohorteCssfv.controller.laboratorio;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ni.com.sts.estudioCohorteCSSFV.modelo.EghResultados;
import ni.com.sts.estudioCohorteCSSFV.modelo.OrdenLaboratorio;
import ni.com.sts.estudioCohorteCSSFV.modelo.ParametrosSistemas;
import ni.com.sts.estudioCohorteCSSFV.modelo.PerifericoResultado;
import ni.com.sts.estudioCohorteCSSFV.modelo.UsuariosView;
import ni.com.sts.estudioCohorteCssfv.datos.laboratorio.LaboratorioDA;
import ni.com.sts.estudioCohorteCssfv.datos.parametro.ParametrosDA;
import ni.com.sts.estudioCohorteCssfv.datos.usuario.UsuariosDA;
import ni.com.sts.estudioCohorteCssfv.dto.OrdenesExamenes;
import ni.com.sts.estudioCohorteCssfv.servicios.LaboratorioService;
import ni.com.sts.estudioCohorteCssfv.servicios.ParametroService;
import ni.com.sts.estudioCohorteCssfv.servicios.UsuariosService;
import ni.com.sts.estudioCohorteCssfv.util.Generico;
import ni.com.sts.estudioCohorteCssfv.util.InfoResultado;
import ni.com.sts.estudioCohorteCssfv.util.Mensajes;
import ni.com.sts.estudioCohorteCssfv.util.UtilDate;
import ni.com.sts.estudioCohorteCssfv.util.Utilidades;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.ComboitemRenderer;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

public class ExtendidoPerifericoController extends SelectorComposer<Component> {

	private static final long serialVersionUID = 1L;

	private static LaboratorioService laboratorioService = new LaboratorioDA();
	
	private static UsuariosService usuariosService = new UsuariosDA();

	private static ParametroService parametroService = new ParametrosDA();
	
	private static final String SELECCIONE = "Seleccione";
	
	private int SEC_PERIFERICO_RESULTADO = 0;
	
	@Wire("[id$=extendidoPeriferico]")
	private Window wextendidoPeriferico;
	
	@Wire("[id$=chkTomaMx]")
    private Checkbox chkTomaMx;
	
	@Wire("[id$=txtAnisocitosis]")
	private Textbox txtAnisocitosis;
	
	@Wire("[id$=txtAnisocromia]")
	private Textbox txtAnisocromia;
	
	@Wire("[id$=txtPoiquilocitosis]")
	private Textbox txtPoiquilocitosis;
	
	@Wire("[id$=txtLinfositosAtipicos]")
	private Textbox txtLinfositosAtipicos;
	
	@Wire("[id$=txtObservaciones]")
	private Textbox txtObservaciones;
	
	@Wire("[id$=txtObservacionesPlaquetaria]")
	private Textbox txtObservacionesPlaquetaria;
	
	//@Wire("[id$=txtCodigoMuestra]")
	//private Textbox txtCodigoMuestra;
	
	@Wire("[id$=cmbBioanalista]")
    private Combobox cmbBioanalista;
	
	@Wire("[id$=btnEnviar]")
	private Button btnEnviar;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		//init();
	}
	
	@Listen("onCreate=[id$=extendidoPeriferico]")
	public void onCreate_extendidoPeriferico() throws Exception{
	  init();
	  
	 }

	private void init() throws Exception {
		cargarBioanalista();
		
		OrdenesExamenes ordenesExamenes = new OrdenesExamenes();
		ordenesExamenes = (OrdenesExamenes) wextendidoPeriferico
				.getAttribute("ordenesExamenes");
		
		PerifericoResultado resultado = laboratorioService.obtenerExtendidoPeriferico(ordenesExamenes.getSecOrdenLaboratorio());
		if(resultado != null && resultado.getSecPerifericoResultado() != 0){
			txtAnisocitosis.setText(resultado.getAnisocitosis());
			txtAnisocromia.setText(resultado.getAnisocromia());
			txtPoiquilocitosis.setText(resultado.getPoiquilocitosis());
			txtLinfositosAtipicos.setText(resultado.getLinfocitosAtipicos());
			txtObservaciones.setText(resultado.getObservacionSblanca());
			txtObservacionesPlaquetaria.setText(resultado.getObservacionPlaqueta());
			//txtCodigoMuestra.setText(resultado.getCodigoMuestra());
			SEC_PERIFERICO_RESULTADO = resultado.getSecPerifericoResultado();
			//bioanalista es el usuario ya registrado
			cargarBioanalista(resultado.getUsuarioBioanalista());
		}else{//bioanalista es el usuario auntenticado
			cargarBioanalista();
		}
		//Se solicita quitar esta restricci�n. Siempre se puede modificar el resultado
		//validarEdicion(ordenesExamenes);
		validarTomaMx();
	}
	
	
	
	
	@Listen("onClick=[id$=btnEnviar]")
    public void btnEnviar_onClick() {
	 try {
        	validarCamposRequerido();
		} catch (Exception e) {
			e.printStackTrace();			
			Mensajes.enviarMensaje(Mensajes.ERROR_PROCESAR_DATOS, Mensajes.TipoMensaje.ERROR);
		}
    }
 
 @Listen("onClick=[id$=btnCerrar]")
    public void btnCerrar_onClick() {
	 wextendidoPeriferico.setAttribute("accionGuardado", false);
	 wextendidoPeriferico.onClose();
    }
 
 private void validarEdicion(OrdenesExamenes ordenExamen){
		//Si el estado de la orden es Enviado no se permite editar ni enviar resultados
		if (ordenExamen.getEstado().equals("Enviado")){
			this.btnEnviar.setDisabled(true);
			this.txtAnisocitosis.setReadonly(true);
			this.txtAnisocromia.setReadonly(true);
			this.txtLinfositosAtipicos.setReadonly(true);
			this.txtObservacionesPlaquetaria.setReadonly(true);
			this.txtObservaciones.setReadonly(true);
			this.txtPoiquilocitosis.setReadonly(true);
			this.cmbBioanalista.setDisabled(true);
			this.chkTomaMx.setDisabled(true);
		}
	}
 
 private void cargarBioanalista() throws Exception{
	 
	 	Generico usuarioBioanalista = new Generico();
	 	usuarioBioanalista.setNumero1(Utilidades.obtenerInfoSesion().getUsuarioId());
	 	usuarioBioanalista.setTexto1(Utilidades.obtenerInfoSesion().getUsername());
	 	Generico seleccione = new Generico();
	 	seleccione.setNumero1(0);
	 	seleccione.setTexto1(SELECCIONE);
	 	List<Generico> oList = new ArrayList<Generico>();
	 	oList.add(0,seleccione);
	 	oList.add(1,usuarioBioanalista);
	 	
	 	//ademas del usuario logueado, cargar el resto de usuarios con el perfil laboratorio
	 	ParametrosSistemas perfilLab = parametroService.getParametroByName("PERFIL_BIOANALISTA");
    	if(perfilLab!=null){
    		List<UsuariosView> usuarios = usuariosService.obtenerUsuariosByPerfiles(perfilLab.getValores().replace(",", "','"));
    		int indice = 2;
    		for(UsuariosView usuario: usuarios){
    			if (!usuario.getId().equals(usuarioBioanalista.getNumero1())){
    				Generico usuarioBio = new Generico();
    				usuarioBio.setNumero1(usuario.getId());
    				usuarioBio.setTexto1(usuario.getUsuario());
    				oList.add(indice,usuarioBio);
    				indice++;
    			}
    		}
    	}
    	
	 	final String idBioanalista = String.valueOf(usuarioBioanalista.getNumero1());
	     this.cmbBioanalista.setModel(new ListModelList<Generico>(new ArrayList<Generico>()));
	     this.cmbBioanalista.setModel(new ListModelList<Generico>(oList));
	     this.cmbBioanalista.setItemRenderer(new ComboitemRenderer<Generico>() {
	            @Override
	            public void render(Comboitem comboitem, Generico fila, int index) throws Exception {
	                comboitem.setLabel( fila.getTexto1());
	                comboitem.setValue(String.valueOf((fila.getNumero1())));
	                if (idBioanalista.equals(comboitem.getValue().toString())) {
	                	cmbBioanalista.setSelectedItem(comboitem);
					}
	        }});
	 }
 
 private void cargarBioanalista(final Short idBioanalista) throws Exception{

	 	Generico seleccione = new Generico();
	 	seleccione.setNumero1(0);
	 	seleccione.setTexto1(SELECCIONE);
	 	Generico usuarioBioanalista = new Generico();
	 	UsuariosView bioanalista = usuariosService.obtenerUsuarioById(idBioanalista.intValue());
	 	usuarioBioanalista.setNumero1(bioanalista.getId());
	 	usuarioBioanalista.setTexto1(bioanalista.getUsuario());
	 	List<Generico> oList = new ArrayList<Generico>();
	 	oList.add(0,seleccione);
	 	oList.add(1,usuarioBioanalista);
	 	
	 	//ademas del usuario logueado, cargar el resto de usuarios con el perfil laboratorio
	 	ParametrosSistemas perfilLab = parametroService.getParametroByName("PERFIL_BIOANALISTA");
    	if(perfilLab!=null){
    		List<UsuariosView> usuarios = usuariosService.obtenerUsuariosByPerfiles(perfilLab.getValores().replace(",", "','"));
    		int indice = 2;
    		for(UsuariosView usuario: usuarios){
    			if (!usuario.getId().equals(bioanalista.getId())){
    				Generico usuarioBio = new Generico();
    				usuarioBio.setNumero1(usuario.getId());
    				usuarioBio.setTexto1(usuario.getUsuario());
    				oList.add(indice,usuarioBio);
    				indice++;
    			}
    		}
    	}
    	
	     this.cmbBioanalista.setModel(new ListModelList<Generico>(new ArrayList<Generico>()));
	     this.cmbBioanalista.setModel(new ListModelList<Generico>(oList));
	     this.cmbBioanalista.setItemRenderer(new ComboitemRenderer<Generico>() {
	            @Override
	            public void render(Comboitem comboitem, Generico fila, int index) throws Exception {
	                comboitem.setLabel( fila.getTexto1());
	                comboitem.setValue(String.valueOf((fila.getNumero1())));
	                if (String.valueOf(idBioanalista).equals(comboitem.getValue().toString())) {
	                	cmbBioanalista.setSelectedItem(comboitem);
					}
	        }});
	 }
 private void validarCamposRequerido() throws Exception{
	 
		if (this.cmbBioanalista.getSelectedIndex() <= 0) {
	         Messagebox.show("Seleccione Bioanalista", "Validaci�n", Messagebox.OK, Messagebox.INFORMATION);
	         return;
	     }
	 /*if (this.txtCodigoMuestra.getValue().length() <= 0){
	     	Messagebox.show("Ingrese C�digo de la Muestra", "Validaci�n", Messagebox.OK, Messagebox.INFORMATION);
	         return;
	     }*/
	 guardaResultadosExtendidoPeriferico();
	 
	}
 
 private void guardaResultadosExtendidoPeriferico() throws Exception{
	 
		PerifericoResultado examenPerifericoResultado = new PerifericoResultado();
		Calendar horaReporte = Calendar.getInstance();
		//SimpleDateFormat sdfHoraReporte = new SimpleDateFormat("hh:mm a");
		 
			OrdenesExamenes ordenesExamenes = new OrdenesExamenes();
			ordenesExamenes = (OrdenesExamenes) wextendidoPeriferico
					.getAttribute("ordenesExamenes");
			

			if(SEC_PERIFERICO_RESULTADO!= 0){
				examenPerifericoResultado.setSecPerifericoResultado(SEC_PERIFERICO_RESULTADO);
			}
		 
			examenPerifericoResultado.setHoraReporte(horaReporte.getTime());
			Short bioanalista = Short.valueOf(this.cmbBioanalista.getSelectedItem().getValue().toString());
			//OrdenLaboratorio ordenLaboratorio = laboratorioService.obtenerOrdenLab(ordenesExamenes.getSecOrdenLaboratorio());
			examenPerifericoResultado.setSecOrdenLaboratorio(ordenesExamenes.getSecOrdenLaboratorio());
			examenPerifericoResultado.setAnisocitosis(this.txtAnisocitosis.getValue());
			examenPerifericoResultado.setAnisocromia(this.txtAnisocromia.getValue());
			examenPerifericoResultado.setPoiquilocitosis(this.txtPoiquilocitosis.getValue());
			examenPerifericoResultado.setLinfocitosAtipicos(this.txtLinfositosAtipicos.getValue());
			examenPerifericoResultado.setObservacionSblanca(this.txtObservaciones.getValue());
			examenPerifericoResultado.setObservacionPlaqueta(this.txtObservacionesPlaquetaria.getValue());
			//examenPerifericoResultado.setCodigoMuestra(this.txtCodigoMuestra.getValue());
			examenPerifericoResultado.setUsuarioBioanalista(bioanalista);
			examenPerifericoResultado.setEstado('1');	 


			 InfoResultado result = laboratorioService.guardarExamenExtendidoPeriferico(examenPerifericoResultado);
			 if(result.isOk() && result.getObjeto()!=null){
				 examenPerifericoResultado = (PerifericoResultado)result.getObjeto();
				 
				 Mensajes.enviarMensaje(Mensajes.REGISTRO_GUARDADO, 
						 Mensajes.TipoMensaje.INFO);
				 wextendidoPeriferico.setAttribute("accionGuardado", true);
				 wextendidoPeriferico.onClose();
				
		   	}else{
		       	Mensajes.enviarMensaje(Mensajes.REGISTRO_NO_GUARDADO, 
		       			Mensajes.TipoMensaje.ERROR);
		       	wextendidoPeriferico.onClose();
		   	}
		 
	} 

	/**
	 * Menejador del evento Check de la casilla de verificaci�n "Muestra Tomada"
	 */
	@Listen("onCheck=[id$=chkTomaMx]")
	public void chkTomaMx_onCheck() {
		OrdenesExamenes ordenesExamenes = new OrdenesExamenes();
		ordenesExamenes = (OrdenesExamenes) wextendidoPeriferico
				.getAttribute("ordenesExamenes");	
		OrdenLaboratorio ordenLaboratorio = laboratorioService.obtenerOrdenLab(ordenesExamenes.getSecOrdenLaboratorio());
		if (!this.chkTomaMx.isChecked()){
			ordenLaboratorio.setTomaMx('0');
			ordenLaboratorio.setFechaHoraTomaMx(null);
		}else{
			ordenLaboratorio.setTomaMx('1');
			ordenLaboratorio.setFechaHoraTomaMx(UtilDate.DateToString(new Date(),"dd/MM/yyyy HH:mm:ss"));
		}
		//INFO, EXCLAM, QUESTION , ERROR
		InfoResultado result = laboratorioService.actualizarOrdenLaboratorio(ordenLaboratorio);
		if(result.isOk() && result.getObjeto()!=null){
			Mensajes.enviarMensaje(result.getMensaje(),	Mensajes.TipoMensaje.INFO);
		}else{
			Mensajes.enviarMensaje(result.getMensaje(),	Mensajes.TipoMensaje.ERROR);
		}
		wextendidoPeriferico.setAttribute("accionTomaMx", true);
	}
	
	/**
	 * Si orden laboratorio tiene toma mx, se marca check sino se desmarca
	 */
	private void validarTomaMx(){
		OrdenesExamenes ordenesExamenes = new OrdenesExamenes();
		ordenesExamenes = (OrdenesExamenes) wextendidoPeriferico
				.getAttribute("ordenesExamenes");	
		OrdenLaboratorio ordenLaboratorio = laboratorioService.obtenerOrdenLab(ordenesExamenes.getSecOrdenLaboratorio());
		if (ordenLaboratorio!=null && ordenLaboratorio.getTomaMx()=='1')
			chkTomaMx.setChecked(true);
		else
			chkTomaMx.setChecked(false);
	}
}
