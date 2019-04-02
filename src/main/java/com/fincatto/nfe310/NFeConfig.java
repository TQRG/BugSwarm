package com.fincatto.nfe310;

import com.fincatto.nfe310.classes.NFAmbiente;
import com.fincatto.nfe310.classes.NFTipoEmissao;
import com.fincatto.nfe310.classes.NFUnidadeFederativa;

import java.io.File;
import java.io.IOException;

public interface NFeConfig {

    String VERSAO_NFE = "3.10";
    String NFE_NAMESPACE = "http://www.portalfiscal.inf.br/nfe";

    NFAmbiente getAmbiente();

    File getCertificado() throws IOException;

    File getCadeiaCertificados() throws IOException;

    String getCertificadoSenha();

    NFUnidadeFederativa getCUF();

    NFTipoEmissao getTipoEmissao();

    /**
     * Lista de protocolos aceitos pela UF, separados por virgula.
     * Ex: TLSv1,TLSv1.1,TLSv1.2
     * @return protocolos aceitos pela UF.
     */
    String getProtocolosSuportados();
}