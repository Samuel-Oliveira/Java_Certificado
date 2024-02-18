package br.com.swconsultoria.certificado;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enum com os possiveis tipos de Certificados A3
 *
 * @author Samuel Oliveira
 */
@AllArgsConstructor
@Getter
@SuppressWarnings("WeakerAccess")
public enum TipoCertificadoA3 {
    LEITOR_SCR3310("SafeWeb", "c:/windows/system32/cmp11.dll"),
    TOKEN_ALADDIN("eToken", "c:/windows/system32/eTpkcs11.dll"),
    LEITOR_GEMPC_PERTO("SmartCard", "c:/windows/system32/aetpkss1.dll"),
    OBERTHUR("Oberthur", "c:/windows/system32/OcsCryptoki.dll"),
    ENTER_SAFE("EnterSafe", "c:/windows/system32/eps2003csp11.dll");

    private final String marca;
    private final String dll;

    public String getConfigA3() {
       return getConfigA3(marca,dll,null);
    }
    public String getConfigA3(String slot) {
       return getConfigA3(marca,dll,slot);
    }
    public String getConfigA3(String marca,String dll) {
       return getConfigA3(marca,dll,null);
    }

    public String getConfigA3(String marca,String dll,String slot) {
        String slotInfo = slot != null ?
                "\n\r" + "slot = " + slot
                : "";

        return "name = " + marca + "\n\r" +
                "library = " + dll +
                slotInfo + "\n\r" +
                "showInfo = true";
    }

}
