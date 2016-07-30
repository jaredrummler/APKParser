/*
 * Copyright (c) 2015, Jared Rummler
 * Copyright (c) 2015, Liu Dong
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the <organization> nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.jaredrummler.apkparser.parser;

import com.jaredrummler.apkparser.model.CertificateMeta;
import com.jaredrummler.apkparser.utils.Utils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.security.cert.CertificateException;
import javax.security.cert.X509Certificate;

public class CertificateParser {

    private final InputStream in;

    public CertificateParser(InputStream in) {
        this.in = new BufferedInputStream(in);
    }

    public CertificateMeta parse() throws IOException, CertificateException {
        X509Certificate certificate = X509Certificate.getInstance(Utils.toByteArray(in));
        CertificateMeta.Builder builder = CertificateMeta.newCertificateMeta();
        byte[] bytes = certificate.getEncoded();
        String certMd5 = md5Digest(bytes);
        String publicKeyString = byteToHexString(bytes);
        String certBase64Md5 = md5Digest(publicKeyString);
        builder.data(bytes);
        builder.certBase64Md5(certBase64Md5);
        builder.certMd5(certMd5);
        builder.startDate(certificate.getNotBefore());
        builder.endDate(certificate.getNotAfter());
        builder.signAlgorithm(certificate.getSigAlgName());
        builder.signAlgorithmOID(certificate.getSigAlgOID());
        return builder.build();
    }

    private String md5Digest(byte[] input) throws IOException {
        MessageDigest digest = getDigest("MD5");
        digest.update(input);
        return getHexString(digest.digest());
    }

    private String md5Digest(String input) throws IOException {
        MessageDigest digest = getDigest("MD5");
        digest.update(input.getBytes(Charset.forName("UTF-8")));
        return getHexString(digest.digest());
    }

    private String byteToHexString(byte[] bArray) {
        StringBuilder sb = new StringBuilder(bArray.length);
        String sTemp;
        for (byte aBArray : bArray) {
            sTemp = Integer.toHexString(0xFF & (char) aBArray);
            if (sTemp.length() < 2) {
                sb.append(0);
            }
            sb.append(sTemp.toUpperCase());
        }
        return sb.toString();
    }

    private String getHexString(byte[] digest) {
        BigInteger bi = new BigInteger(1, digest);
        return String.format("%032x", bi);
    }

    private MessageDigest getDigest(String algorithm) {
        try {
            return MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

}
