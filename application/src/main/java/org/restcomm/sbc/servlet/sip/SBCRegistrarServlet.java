/*******************************************************************************
 * TeleStax, Open Source Cloud Communications
 * Copyright 2011-2016, Telestax Inc, Eolos IT Corp and individual contributors
 * by the @authors tag.
 *
 * This program is free software: you can redistribute it and/or modify
 * under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */


package org.restcomm.sbc.servlet.sip;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.sip.SipFactory;
import javax.servlet.sip.SipServlet;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;

import org.apache.commons.configuration.Configuration;
import org.apache.log4j.Logger;
import org.restcomm.chain.processor.impl.SIPMutableMessage;
import org.restcomm.sbc.ConfigurationCache;
import org.restcomm.sbc.chain.impl.registrar.UpstreamRegistrarProcessorChain;
import org.restcomm.sbc.chain.impl.registrar.DownstreamRegistrarProcessorChain;


/**
 * @author  ocarriles@eolos.la (Oscar Andres Carriles)
 * @date    3/5/2016 22:50:15
 * @class   SBCRegistrarServlet.java
 *
 */
public class SBCRegistrarServlet extends SipServlet {	
	private static final long serialVersionUID = 1L;	
	private Configuration configuration;
	private SipFactory sipFactory;	
	
	
	private UpstreamRegistrarProcessorChain upChain;
	private DownstreamRegistrarProcessorChain dwChain;
	private ServletConfig config;
	private static transient Logger LOG = Logger.getLogger(SBCRegistrarServlet.class);
	
	@Override
	public void init(ServletConfig servletConfig) throws ServletException {
		if(LOG.isInfoEnabled()){
	          LOG.info(">> Registrar Servlet init()");
	    }
		super.init(servletConfig);
		this.config=servletConfig;
		sipFactory = (SipFactory) getServletContext().getAttribute(SIP_FACTORY);
		final ServletContext context = servletConfig.getServletContext();
		configuration=(Configuration) context.getAttribute(Configuration.class.getName());
		//ConfigurationCache.build(sipFactory, configuration);
		
		upChain=new UpstreamRegistrarProcessorChain();
		LOG.info("Loaded (v. "+upChain.getVersion()+") "+upChain.getName());
		dwChain=new DownstreamRegistrarProcessorChain();
		LOG.info("Loaded (v. "+dwChain.getVersion()+") "+dwChain.getName());
		
		
		
	}
	
	
	@Override
	protected void doRegister(SipServletRequest sipServletRequest) throws ServletException, IOException {
		 LOG.info("OUTB IFCES="+config.getServletContext().getAttribute("javax.servlet.sip.outboundInterfaces"));
		 upChain.process(new SIPMutableMessage(sipServletRequest));
		
		
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	protected void doResponse(SipServletResponse sipServletResponse) throws ServletException, IOException {
			
		dwChain.process(new SIPMutableMessage(sipServletResponse));
		
		super.doResponse(sipServletResponse);
	}
	
	/**
	 * {@inheritDoc}
	 */
	protected void doErrorResponse(SipServletResponse sipServletResponse) throws ServletException, IOException {
		

	}
	
	
}
