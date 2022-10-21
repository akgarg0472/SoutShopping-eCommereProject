const navigate = (url) => {
    if (url.trim() === '/user/logout') {
        swal({
            title: "Are you sure want to logout?",
            text: "",
            icon: "warning",
            buttons: true,
            dangerMode: true,
        })
            .then((willDelete) => {
                if (willDelete) {
                    window.location = url;
                } else {
                    // todo nothing
                }
            });
    } else {
        window.location = url;
    }
}


const addToCart = (cartProduct) => {
    const productLoader = $("#product-loader");

    $.get("/checkout/isUserLoggedIn")
        .done((response) => {
            if (response === true) {
                $.get("/checkout/isUserLoggedIn")
                    .done((resp) => {
                        if (resp === true) {
                            productLoader.show();
                            $.post("/user/add-cart-item", cartProduct)
                                .done((res) => {
                                    productLoader.hide();
                                    if (res === true) {
                                        productLoader.hide();
                                        updateCart();
                                        swal('Product added to cart');
                                    } else {
                                        swal('Unable to add to cart. Try again later');
                                    }
                                })
                                .fail(() => {
                                    productLoader.hide();
                                    swal("Error adding to the cart");
                                })
                        }
                    })
            } else {
                swal("Please login to perform this action");
            }
        })
        .fail(() => {
            swal("Something went wrong. Please try again later");
        });
}


const updateCart = () => {
    $.get("/checkout/isUserLoggedIn")
        .done((response) => {
            if (response === true) {
                $.get('/user/getUserCartItem')
                    .done((response) => {
                        $("#total-cart-items").html(response);
                    })
                    .fail(() => {
                        // todo nothing
                    })
            } else {
                $("#total-cart-items").html("0");
            }
        })
        .fail(() => {
            $("#total-cart-items").html("0");
        });
}


const deleteCartOrder = (orderId) => {
    $.get("/checkout/isUserLoggedIn")
        .done((response) => {
            if (response === true) {
                window.location = `/user/delete-cart-order/${orderId}`;
            }
        })
        .fail(() => {
            swal("Something went wrong!")
        })
}


const buyProduct = (productToBuy) => {
    const productLoader = $("#product-loader");

    $.get('/checkout/isUserLoggedIn')
        .done((response) => {
            if (response === true) {
                productLoader.show();
                $.post('/user/process-buy-product', productToBuy)
                    .done((resp) => {
                        productLoader.hide();
                        if (resp === true) {
                            window.location = "/user/buy-product-checkout";
                        }
                    })
                    .fail(() => {
                        productLoader.hide();
                        console.log("buy product failed");
                    })
            } else {
                swal('Please login to buy product');
            }
        })
        .fail(() => {
            // todo later
        })
}


$(document).ready(() => {
    updateCart();
    $("#product-loader").hide()
})