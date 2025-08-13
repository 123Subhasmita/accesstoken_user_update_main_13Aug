package org.gluu.agama.smtp;

import java.util.Map;

class SendEmailTemplateAr {

    static Map<String, String> get(String username) {

        String html = """
<table role="presentation" cellspacing="0" cellpadding="0" width="100%" 
       style="background-color:#F2F4F6;margin:0;padding:0;width:100%;" 
       dir="rtl" lang="ar">
  <tbody>
    <tr>
      <td align="center">
        <table role="presentation" cellspacing="0" cellpadding="0" width="100%" style="margin:0;padding:0;">
          <tbody>
            <!-- Logo -->
            <tr>
              <td align="center" style="padding:25px 0;text-align:center;">
                <img src="https://storage.googleapis.com/email_template_staticfiles/Phi_logo320x132_Aug2024.png" width="160" alt="Phi Logo" style="border:none;">
              </td>
            </tr>

            <!-- Main Email Body -->
            <tr>
              <td style="width:100%;margin:0;padding:0;">
                <table role="presentation" cellspacing="0" cellpadding="0" width="570" align="center" style="background-color:#FFFFFF;margin:0 auto;padding:0;border-radius:4px;">
                  <tbody>
                    <tr>
                      <td style="padding:45px;font-family:'Nunito Sans',Helvetica,Arial,sans-serif;color:#51545E;font-size:16px;line-height:1.625;">
          
                        <p>مرحباً،</p>
                        <p>خبر رائع! تم إنشاء اسم المستخدم الخاص بك بنجاح.</p>

                        <p>اسم المستخدم: <span style="font-weight: bold;">""" + username + """</span></p>
                        
                        <p>يمكنك الآن استخدام اسم المستخدم بدلاً من بريدك الإلكتروني لتسجيل الدخول، مما يجعل تجربتك أكثر سلاسة وأمانًا.</p>

                        <p><span style="font-weight: bold;">لكن هذا ليس كل شيء!</span></p>

                        <p>نحن لا نقوم فقط بتحسين طريقة تسجيل الدخول لديك، بل نُعِد حسابك لميزات جديدة في الطريق، مصممة لدعم رحلتك نحو مستقبل مالي أكثر ازدهارًا.</p>
                        <p>إذا كانت لديك أي أسئلة، لا تتردد في التواصل معنا.</p>
                        <p>مع أطيب التحيات،<br>فريق Phi Wallet</p>

                      </td>
                    </tr>
                  </tbody>
                </table>
              </td>
            </tr>

            <!-- Footer -->
            <tr>
              <td>
                <table role="presentation" cellspacing="0" cellpadding="0" width="570" align="center" style="margin:0 auto;padding:0;text-align:center;">
                  <tbody>
                    <tr>
                      <td style="padding:20px;font-size:12px;color:#666;">
                        <div style="direction:ltr; display:inline-block; text-align:left;">
                          <p style="margin:0 0 10px 0;font-size:14px;font-weight:bold;color:#565555; direction:rtl; text-align:right;">تابعنا على:</p>
                          <p style="margin:0;">
                            <a href="https://www.facebook.com/PhiWallet" style="margin:0 5px;"><img src="https://storage.googleapis.com/mwapp_prod_bucket/social_icon_images/facebook.png" style="height:20px;"></a>
                            <a href="https://x.com/PhiWallet" style="margin:0 5px;"><img src="https://storage.googleapis.com/mwapp_prod_bucket/social_icon_images/twitter.png" style="height:20px;"></a>
                            <a href="https://www.instagram.com/phi.wallet" style="margin:0 5px;"><img src="https://storage.googleapis.com/mwapp_prod_bucket/social_icon_images/instagram.png" style="height:20px;"></a>
                            <a href="https://www.linkedin.com/company/phiwallet" style="margin:0 5px;"><img src="https://storage.googleapis.com/mwapp_prod_bucket/social_icon_images/linkedin.png" style="height:20px;"></a>
                          </p>
                        </div>
                        <p style="margin-top:10px;line-height:20px;color:#A8AAAF;font-size:12px;">
                          Phi Wallet Unipessoal LDA<br>
                          Avenida da Liberdade 262 R/C<br>
                          1250-149 Lisbon<br>
                          Portugal
                        </p>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </td>
            </tr>


          </tbody>
        </table>
      </td>
    </tr>
  </tbody>
</table>
""";

        return Map.of(
            "subject", "تم إنشاء اسم المستخدم الخاص بك بنجاح",
            "body", html
        );
    }
}
