/**
 * <copyright>
 * (C) Copyright 2011 CSI-PIEMONTE;

 * Concesso in licenza a norma dell'EUPL, esclusivamente versione 1.1;
 * Non e' possibile utilizzare l'opera salvo nel rispetto della Licenza.
 * E' possibile ottenere una copia della Licenza al seguente indirizzo:
 *
 * http://www.eupl.it/opensource/eupl-1-1
 *
 * Salvo diversamente indicato dalla legge applicabile o concordato per 
 * iscritto, il software distribuito secondo i termini della Licenza e' 
 * distribuito "TAL QUALE", SENZA GARANZIE O CONDIZIONI DI ALCUN TIPO,
 * esplicite o implicite.
 * Si veda la Licenza per la lingua specifica che disciplina le autorizzazioni
 * e le limitazioni secondo i termini della Licenza.
 * </copyright>
 *
 * $Id$
 */
package mddtools.preferences;

import mddtools.usagetracking.ProfilingPacketBuilder;

/**
 * Constant definitions for plug-in preferences
 */
public class PreferenceConstants {

//	public static final String P_PATH = "pathPreference";
//
//	public static final String P_BOOLEAN = "booleanPreference";
//
//	public static final String P_CHOICE = "choicePreference";
//
//	public static final String P_STRING = "stringPreference";
	public static final String P_NOME = ProfilingPacketBuilder.P_WHO_NAME;
	
	public static final String P_COGNOME = ProfilingPacketBuilder.P_WHO_SURNAME;
	
	public static final String P_EMAIL = ProfilingPacketBuilder.P_WHO_EMAIL;
	
	public static final String P_COMPANY = ProfilingPacketBuilder.P_WHO_COMPANY;
}
