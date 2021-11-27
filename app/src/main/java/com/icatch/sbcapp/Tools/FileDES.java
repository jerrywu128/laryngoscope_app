package com.icatch.sbcapp.Tools;

import android.content.Context;
import android.content.SharedPreferences;

import com.icatch.sbcapp.GlobalApp.GlobalInfo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.Key;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class FileDES {
    /**加密解密的key*/
    private Key mKey;
    /**解密的密码*/
    private Cipher mDecryptCipher;
    /**加密的密码*/
    private Cipher mEncryptCipher;
    private final static String HEX = "0123456789ABCDEF";
    private  static final String  SHA1PRNG="SHA1PRNG";
    public FileDES(String key) throws Exception
    {
        initKey(key);
        initCipher();
    }
    public static String getPbKey(){
        Boolean isFirstGet = false;
        SharedPreferences pref = GlobalInfo.getInstance().getAppContext().getSharedPreferences("myActivityName", 0);
        isFirstGet = pref.getBoolean("isFirstGet", true);
        if(isFirstGet){
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("PB_password",generateKey());
            editor.commit();
        }
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("isFirstGet", false);
        editor.commit();

        return pref.getString("PB_password","test.key");
    }
    public static String generateKey() {
        try {
            SecureRandom localSecureRandom = SecureRandom.getInstance(SHA1PRNG);
            byte[] bytes_key = new byte[10];
            localSecureRandom.nextBytes(bytes_key);
            String str_key = toHex(bytes_key);
            return str_key;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    //二进制转字符
    public static String toHex(byte[] buf) {
        if (buf == null)
            return "";
        StringBuffer result = new StringBuffer(2 * buf.length);
        for (int i = 0; i < buf.length; i++) {
            appendHex(result, buf[i]);
        }
        return result.toString();
    }

    private static void appendHex(StringBuffer sb, byte b) {
        sb.append(HEX.charAt((b >> 4) & 0x0f)).append(HEX.charAt(b & 0x0f));
    }

    /**
     * 创建一个加密解密的key
     * @param keyRule
     */
    public void initKey(String keyRule) throws UnsupportedEncodingException {
        try {
            DESKeySpec dks = new DESKeySpec(keyRule.getBytes());
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            //key的长度不能够小于8位字节
            mKey = keyFactory.generateSecret(dks);
        } catch (Exception e) {
            throw new RuntimeException("Error initializing SqlMap class. Cause: " + e);
        }

    }

    /***
     * 初始化加载密码
     * @throws Exception
     */
    private void initCipher() throws Exception
    {
        mEncryptCipher = Cipher.getInstance("DES");
        mEncryptCipher.init(Cipher.ENCRYPT_MODE, mKey);

        mDecryptCipher = Cipher.getInstance("DES");
        mDecryptCipher.init(Cipher.DECRYPT_MODE, mKey);
    }

    /**
     * 加密圖檔文件
     * @param in
     * @param savePath 加密后保存的位置
     */
    public void doEncryptFile(InputStream in, String savePath)
    {
        if(in==null)
        {
            System.out.println("inputstream is null");
            return;
        }
        try {
            CipherInputStream cin = new CipherInputStream(in, mEncryptCipher);
            OutputStream os = new FileOutputStream(savePath);
            byte[] bytes = new byte[1024];
            int len = -1;
            while((len=cin.read(bytes))>0)
            {
                os.write(bytes, 0, len);
                os.flush();
            }
            os.close();
            cin.close();
            in.close();
            System.out.println("enacc");
        } catch (Exception e) {
            System.out.println("enerr");
            e.printStackTrace();
        }
    }

    /**
     * 加密圖檔文件
     * @param filePath 需要加密的文件路径
     * @param savePath 加密后保存的位置
     * @throws FileNotFoundException
     */
    public void doEncryptFile(String filePath,String savePath) throws FileNotFoundException
    {
        doEncryptFile(new FileInputStream(filePath), savePath);
    }
    public void doDecryptFile(InputStream in, String path) {
        if (in == null) {
            System.out.println("inputstream is null");
            return;
        }
        try {
            CipherInputStream cin = new CipherInputStream(in, mDecryptCipher);
            OutputStream outputStream = new FileOutputStream(path);
            byte[] bytes = new byte[1024];
            int length = -1;
            while ((length = cin.read(bytes)) > 0) {
                outputStream.write(bytes, 0, length);
                outputStream.flush();
            }
            cin.close();
            in.close();
            System.out.println("解密成功");
        } catch (Exception e) {
            System.out.println("解密失败");
            e.printStackTrace();
        }
    }

    /**
     * 解密圖檔文件
     *
     * @param filePath 文件路径
     * @throws Exception
     */
    public void doDecryptFile(String filePath, String outPath) throws Exception {
        doDecryptFile(new FileInputStream(filePath), outPath);
    }


    private final int REVERSE_LENGTH = 100;
    /**
     * 加密視頻文件
     *
     * @param strFile 源文件绝对路径
     * @return
     */
    public boolean encrypt(String strFile) {
        int len = REVERSE_LENGTH;
        try {
            File f = new File(strFile);
            RandomAccessFile raf = new RandomAccessFile(f, "rw");
            long totalLen = raf.length();

            if (totalLen < REVERSE_LENGTH)
                len = (int) totalLen;

            FileChannel channel = raf.getChannel();
            MappedByteBuffer buffer = channel.map(
                    FileChannel.MapMode.READ_WRITE, 0, REVERSE_LENGTH);
            byte tmp;
            for (int i = 0; i < len; ++i) {
                byte rawByte = buffer.get(i);
                tmp = (byte) (rawByte ^ i);
                buffer.put(i, tmp);
            }
            buffer.force();
            buffer.clear();
            channel.close();
            raf.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 解密視頻文件
     */



    public boolean decrypt(String filePath) {
        int len = REVERSE_LENGTH;
        try {
            File f = new File(filePath);
            RandomAccessFile raf = new RandomAccessFile(f, "rw");
            long totalLen = raf.length();

            if (totalLen < REVERSE_LENGTH)
                len = (int) totalLen;

            FileChannel channel = raf.getChannel();
            MappedByteBuffer buffer = channel.map(
                    FileChannel.MapMode.READ_WRITE, 0, REVERSE_LENGTH);
            byte tmp;
            for (int i = 0; i < len; ++i) {
                byte rawByte = buffer.get(i);
                tmp = (byte) (rawByte ^ i);
                buffer.put(i, tmp);
            }
            buffer.force();
            buffer.clear();
            channel.close();
            raf.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }




}
