$.getScript('https://checkout.razorpay.com/v1/checkout.js', () => {
})

const initiatePayment = (amount) => {
    const address = $("#si-address").text().substr(9);
    const city = $("#si-city").text().substr(6);
    const state = $("#si-state").text().substr(7);
    const zipcode = $("#si-zipcode").text().substr(9);
    const country = $("#si-country").text().substr(9);

    if (address === '' || city === '' || state === '' || zipcode === '0' || country === '') {
        swal('Please update your shipping address info');
    } else {
        if (amount != null && amount.trim() !== '' && parseInt(amount) > 0) {
            $.post('/payment/process-payment', {
                'amount': amount
            })
                .done((response) => {
                    if (response !== false) {
                        const order = JSON.parse(response);
                        if (order.status === 'created') {
                            let options = {
                                key: 'rzp_test_R2q7xS6rUEAAlQ',
                                amount: order.amount,
                                currency: 'INR',
                                name: 'SoutShopping',
                                description: 'Order payment',
                                order_id: order.id,
                                handler: (odr) => {
                                    $("#order-loader").show();
                                    $.post('/payment/fetch-success-payment', {
                                        amount: order.amount,
                                        order_id: odr.razorpay_order_id,
                                        payment_id: odr.razorpay_payment_id,
                                        signature: odr.razorpay_signature
                                    })
                                        .done((res) => {
                                            $("order-loader").hide();
                                            res = JSON.parse(res);
                                            if (res.response === true) {
                                                window.location = res.responseURL;
                                            }
                                        })
                                        .fail(() => {
                                            $("order-loader").hide();
                                        })
                                },
                                prefill: {
                                    name: '',
                                    email: '',
                                    contact: ''
                                },
                                notes: {
                                    address: 'SoutShopping, India'
                                },
                                theme: '#ffffff'
                            }

                            let rzp = new Razorpay(options);
                            rzp.on('payment.failed', (order) => {
                                order.error.code,
                                    order.error.description,
                                    order.error.source,
                                    order.error.step,
                                    order.error.reason,
                                    order.error.metadata.order_id,
                                    order.error.metadata.payment_id,
                                    alert('Payment Failed')
                            });

                            rzp.open();
                        }
                    } else {
                        swal('Please login to complete your payment');
                    }
                })
                .fail((response) => {
                    console.log(response);
                    alert('Error processing your payment');
                })
        } else {
            swal('Invalid or zero amount');
        }
    }
}