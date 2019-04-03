/**
 * 
 */
package pt.gov.dgarq.roda.common.certification;

import javax.swing.JApplet;

/**
 * @author Luis Faria
 * 
 */
public class SignatureVerifyApplet extends JApplet {

	private static final long serialVersionUID = 1433330989863481650L;

	private SignatureVerifyPanel signatureVerifyPanel = null;

	/**
	 * Create a new signature verify applet
	 */
	public SignatureVerifyApplet() {

	}

	@Override
	public void init() {
		add(getSignatureVerifyPanel());
	}

	protected SignatureVerifyPanel getSignatureVerifyPanel() {
		if (signatureVerifyPanel == null) {
			signatureVerifyPanel = new SignatureVerifyPanel();
		}
		return signatureVerifyPanel;
	}

}
