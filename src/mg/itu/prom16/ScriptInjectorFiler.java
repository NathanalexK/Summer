package mg.itu.prom16;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

import java.io.IOException;

//@WebFilter(urlPatterns = {"*.jsp", "/*"})
public class ScriptInjectorFiler implements Filter {



    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        CharResponseWrapper crw = new CharResponseWrapper(((HttpServletResponse) servletResponse));

        System.out.println("filteeeeerreessssdsfsdf");
        filterChain.doFilter(servletRequest, crw);

        String originalHtml = crw.toString();
        String modifiedHtml = originalHtml.replace("</head>", script + "</head>");
        servletResponse.getWriter().write(modifiedHtml);
    }

    private final String script =
        """
            <script>
            document.addEventListener('DOMContentLoaded', () => {
                console.log("It works");
                const form = document.getElementsByTagName('form')[0];
            
                form.addEventListener('submit', (evt) => {
                    evt.preventDefault();
            
                    const xhr = new XMLHttpRequest();
                    const formData = new FormData(form);
            
                    xhr.addEventListener('readystatechange', () => {
                        if(xhr.readyState < 4) return;
                       \s
                        if(xhr.status === 500) {
                            const response = JSON.parse(xhr.response);
                            console.log(response);
                            const errorElements = document.querySelectorAll('.form-error');
                            
                            errorElements.forEach((element) => {
                                element.remove();
                            });
                            
                            
                            Object.entries(response).forEach(([key, value]) => {
                                const input = document.querySelector('input[name="' + key + '"]' );
                                console.log(key);
                                console.log(input);
                                let pElement = document.createElement('p');
                                pElement.className = 'form-error';
                                pElement.textContent = value;
                                input.insertAdjacentElement('afterend', pElement);
                            });
                        }
                       \s
                        else {
                            console.log("OK");
                            //form.removeEventListener('submit', arguments.callee);
                            //form.submit();
                        }
                    });
            
                    xhr.open(form.method, form.action, false);
                    xhr.send(formData);
                });
            });
            </script>
        """;
}
