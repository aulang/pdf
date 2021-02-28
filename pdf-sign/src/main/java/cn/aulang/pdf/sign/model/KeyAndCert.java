package cn.aulang.pdf.sign.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.security.PrivateKey;
import java.security.cert.Certificate;

/**
 * @author aulang
 * @date 2021-01-15 15:13
 */
@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class KeyAndCert {

    private PrivateKey key;
    private Certificate[] chain;

}
