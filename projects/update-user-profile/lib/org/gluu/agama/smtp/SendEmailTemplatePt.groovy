package org.gluu.agama.smtp;

import java.util.Map;

class SendEmailTemplatePt {

    static Map<String, String> get(String username) {

        String html = """
<table role="presentation" cellspacing="0" cellpadding="0" width="100%" style="background-color:#F2F4F6;margin:0;padding:0;width:100%;">
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
                        
                        <p>Olá User,</p>
                       
                        <p>Parabéns! O teu nome de utilizador foi criado com sucesso.</p>

                        <p>Nome de utilizador: <span style="font-weight: bold;">""" + username + """</span></p>

                        <p>Agora já podes usar o teu nome de utilizador em vez do e-mail para iniciares sessão, tornando a tua experiência mais simples e segura.</p>

                        <p><span style="font-weight: bold;">Mas isso não é tudo!</span></p>

                        <p>Não estamos apenas a melhorar a forma como inicias sessão, estamos a preparar o terreno para algo entusiasmante. Novas funcionalidades estão a caminho, desenhadas para impulsionar o teu percurso rumo a um futuro financeiro mais próspero.</p>
                        <p>O melhor ainda está para vir!</p>
                        <p>Entretanto, se tiveres alguma dúvida ou precisares de ajuda, estamos apenas a um clique de distância.</p>
                        <p>Com os melhores cumprimentos,<br>Equipa Phi Wallet</p>

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
                        <p style="margin:0 0 10px 0;font-size:14px;font-weight:bold;color:#565555;">Siga-nos em:</p>
                        <p>
                          <a href="https://www.facebook.com/PhiWallet" style="margin:0 5px;"><img src="https://storage.googleapis.com/mwapp_prod_bucket/social_icon_images/facebook.png" style="height:20px;"></a>
                          <a href="https://x.com/PhiWallet" style="margin:0 5px;"><img src="https://storage.googleapis.com/mwapp_prod_bucket/social_icon_images/twitter.png" style="height:20px;"></a>
                          <a href="https://www.instagram.com/phi.wallet" style="margin:0 5px;"><img src="https://storage.googleapis.com/mwapp_prod_bucket/social_icon_images/instagram.png" style="height:20px;"></a>
                          <a href="https://www.linkedin.com/company/phiwallet" style="margin:0 5px;"><img src="https://storage.googleapis.com/mwapp_prod_bucket/social_icon_images/linkedin.png" style="height:20px;"></a>
                        </p>
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
            "subject", "O teu nome de utilizador foi criado com sucesso",
            "body", html
        );
    }
}
