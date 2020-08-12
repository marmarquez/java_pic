package ledon3fazt;

import java.awt.Color;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import com.fazecast.jSerialComm.*;

public class guiledon3 {

	SerialPort port;								// definicion del objeto del Puerto Serial
	boolean flag = false;										// auxiliar para boton LED
	boolean f = false;											// auxiliar para saber estado de conexion
	
    byte[] on = new byte[] { (byte) 78 };			// en ascii 'N' para encender
    byte[] off = new byte[] { (byte) 70 };			// en ascii 'F' para apagar
    
    byte[] send = new byte[] { (byte) 80 };				// byte de envio de conexion 'P'

	private JFrame frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					guiledon3 window = new guiledon3();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public guiledon3() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 326);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Presione el bot\u00F3n para Encender o Apagar el LED:");
		lblNewLabel.setBounds(64, 29, 284, 14);
		frame.getContentPane().add(lblNewLabel);
		
		final JLabel lblNewLabel_1 = new JLabel("Desconectado");
		lblNewLabel_1.setBounds(180, 161, 88, 14);
		frame.getContentPane().add(lblNewLabel_1);
		
		
		//-- BOTON PARA CONEXION
		
		JButton btnNewButton = new JButton("Conectar");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
			    SerialPort[] ports = SerialPort.getCommPorts();		// obtener lista de puertos disponibles

			    byte[] recepcion = new byte[] { (byte) 0};			// byte de recepcion de peticion de conexion

			    for( int i = 0; i < ports.length; i++ ) { 

			        String systemPortName = ports[i].getSystemPortName();		// nombre del puerto en cuestion
			        port = SerialPort.getCommPort(systemPortName);				// se establece el puerto

		        	System.out.println(systemPortName);				// DEBUG

			        if(port.openPort()) 		// intentar abrir puerto systemPortName
			        {
			        	System.out.println("Puerto abierto");		// DEBUG
			        	
			        	port.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 5000, 0);
			        	
                        try 
                        {
                        	
    			            port.writeBytes(send, 1);				// manda 'P' para peticion de conexion

    			            int bytesRead = port.readBytes(recepcion, 1);	// leer buffer de rx
    			            
                        	System.out.println("RECIBIDO");					// DEBUG
                        	System.out.println(bytesRead);					// DEBUG
                        	
         
    			            if (recepcion[0] == 80)        // si responden con P se conecta a ese puerto y se sale del for
    			            {
    			            	
                            	System.out.println("CONECTADO");			// DEBUG
                            	lblNewLabel_1.setText("Conectado");			// se cambia msj de label de conexion
                            	f = true;									// conectado
    				            break; 
    			            }
    			            else 	// si no recibo nada o recibo otra cosa cierro el puerto
    			            {
    			            	port.closePort();
                            	f = false;								// desconectado
    			            }
    			            
    			        	// si no se conecta sigue el ciclo y prueba con otro puerto
                            
                        }
                        catch ( Exception e1)		// error en la tx o rx
                        {
			            	port.closePort();
                        	System.out.println(e1);				// DEBUG
                        	f = false;							// desconectado

                        }
      			
			        } 
			        else 		// si no se puede abrir el puerto
			        {
			            System.out.println("Unable to open the port.");			// DEBUG
                    	f = false;						// desconectado
			                        
			        }
			    }
			    
			    if (f == false) {
			    	
                    JOptionPane.showMessageDialog(null, "Conexión fallida.");   // se muestra msj de conexion fallida
                	lblNewLabel_1.setText("Desconectado");						// label en esatdo Desconectado

			    }
			    
			    // port.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);
			    port.setComPortParameters(115200, 8, 1,0);
				
				
			}
		});
		btnNewButton.setBounds(167, 211, 101, 37);
		frame.getContentPane().add(btnNewButton);
		
		
		//-- BOTON PARA ENCENDER Y APAGAR EL LED
		
		final JButton btnNewButton_1 = new JButton("LED OFF");
		btnNewButton_1.setBackground(Color.RED);
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
								
				if (f == true)  		// si esta conectado
				{
					while (1 == 1) 
					{
						if (flag == false)		// encender led
						{
							try 
							{
								btnNewButton_1.setBackground(Color.green); 			// cambia aspecto del boton
								btnNewButton_1.setText("LED ON");
								
								flag = true;
	    			            port.writeBytes(on, 1);				// manda 'N' para encender
	    			            
								break;
							}
							catch(Exception e2) 
							{
								
							}

						}

						if (flag == true)		// apagar led
						{	
							try 
							{
								btnNewButton_1.setBackground(Color.red);		// cambia aspecto de boton
								btnNewButton_1.setText("LED OFF");
								
								flag = false;
	    			            port.writeBytes(off, 1);				// manda 'F' para apagar
	    			            
								break;
							}
							catch(Exception e2) 
							{
								
							}
						}	
						
					}
				}
				else  			// si no esta conectado muestro un mensaje...
				{
		            System.out.println("Puerto Desconectado");				// DEBUG
                    JOptionPane.showMessageDialog(null, "Sin conexion");
				}
				
				
			}
		});
		btnNewButton_1.setBounds(167, 60, 101, 37);
		frame.getContentPane().add(btnNewButton_1);
		
	}
}
