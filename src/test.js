document.addEventListener('DOMContentLoaded', () => {
    const form = document.getElementsByTagName('form')[0];

    form.addEventListener('submit', (evt) => {
        evt.preventDefault();

        const xhr = new XMLHttpRequest();
        const formData = new FormData(form);

        xhr.addEventListener('readystatechange', () => {
            if(xhr.readyState < 4) return;

            if(xhr.status === 500) {
                const response = JSON.parse(xhr.response);
                console.log(response);

                Object.entries(response).forEach(([key, value]) => {
                    const input = document.getElementsByName(key)[0];
                    const p = document.createElement('p');
                    p.textContent = value;
                    p.className = 'form_error';
                    input.insertAdjacentElement('afterend', p);
                })

                // ) {
                //     const input = document.getElementsByName(key)[0];
                //     const p = document.createAttribute('p');
                //     p.textContent = response
                // }
            }

            else {
                console.log("OK");
            }
        });

        xhr.open(form.method, form.action, false);
        xhr.send(formData);
    })

})