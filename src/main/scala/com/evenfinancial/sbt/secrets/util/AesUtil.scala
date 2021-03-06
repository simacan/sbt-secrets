package com.evenfinancial.sbt.secrets.util

import java.util.Base64
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.spec.{IvParameterSpec, SecretKeySpec}

// Basically copied from the Play framework's Crypto library.
// @see https://github.com/playframework/playframework/blob/2.4.x/framework/src/play/src/main/scala/play/api/libs/Crypto.scala
object AesUtil {

  def encrypt(data: String, secretKeySpec: SecretKeySpec): String = {
    val cipher = buildCipher()
    cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec)
    val encrypted = cipher.doFinal(data.getBytes("UTF-8"))
    val result = cipher.getIV ++ encrypted
    Base64.getEncoder.encodeToString(result)
  }

  def decrypt(data: String, secretKeySpec: SecretKeySpec): String = {
    val bytes = Base64.getDecoder.decode(data)
    val cipher = buildCipher()
    val iv = bytes.slice(0, cipher.getBlockSize)
    val payload = bytes.slice(cipher.getBlockSize, bytes.size)
    cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, new IvParameterSpec(iv))
    new String(cipher.doFinal(payload), "utf-8")
  }

  private def buildCipher(): Cipher = Cipher.getInstance("AES/CTR/NoPadding")

  def buildSecretKey(dataKey: String, keySizeBits: Int): SecretKeySpec = {
    val algorithm = "AES"
    val messageDigest = MessageDigest.getInstance("SHA-256")
    messageDigest.update(dataKey.getBytes("utf-8"))
    val raw = messageDigest.digest().slice(0, keySizeBits / 8)
    new SecretKeySpec(raw, algorithm)
  }

}
