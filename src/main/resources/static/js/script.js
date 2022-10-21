$("#send-otp-button").on('click', () => {
    $("#forgot-pass-loader").css('display', 'flex');
})


$("#verify-otp-button").on('click', () => {
    $("#otp-loader").css('display', 'flex');
})


$("#password-confirm-button").on('click', () => {
    $("#new-pass-loader").css('display', 'flex');
})


$("#forgot-password-form").on('submit', (event) => {
    event.preventDefault();

    const fpSection = $("#forgot-password-section");
    const otpSection = $("#forgot-password-otp-section");
    const newPassSection = $("#new-password-section");
    const fpForm = $("#forgot-password-form");
    const otpForm = $("#forgot-password-otp-form");

    $.post("/forgot-password/process-forgot-password", fpForm.serialize())
        .done((response) => {
            $("#forgot-pass-loader").hide();

            if (response === "SUCCESS") {
                fpSection.hide();
                otpSection.show();
                otpForm.on('submit', (e) => {
                    e.preventDefault();
                    $.post("/forgot-password/verify-otp", otpForm.serialize())
                        .done((otpResponse) => {
                            $("#otp-loader").hide();
                            if (otpResponse === 'VERIFIED') {
                                swal('OTP verified');
                                otpSection.hide();
                                newPassSection.show();
                                changePassword();
                            } else if (otpResponse === 'MISMATCHED') {
                                swal('OTP Mismatched');
                            } else {
                                swal("Internal Server error");
                            }
                        })
                        .fail(() => {
                            $("#otp-loader").hide();
                            swal("Internal server error");
                        })
                })
            } else if (response === "USER_NOT_FOUND") {
                swal('Email id is not registered');
            } else {
                swal("Internal Server error");
            }
        })
        .fail(() => {
            $("#forgot-pass-loader").hide();
            swal("Internal Server error");
        })
})


const changePassword = () => {
    const newPassForm = $("#new-password-form");

    newPassForm.on('submit', (e) => {
            e.preventDefault();
            const newPass = $("#new-password").val();
            const confNewPass = $("#confirm-new-password").val();

            if (newPass === confNewPass) {
                $.post("/forgot-password/process", newPassForm.serialize())
                    .done((resp) => {
                        $("#new-pass-loader").hide();
                        if (resp === 'PSU') {
                            window.location = "/login";
                        } else if (resp === 'OPM') {
                            swal("Old password is incorrect");
                        } else if (resp === 'ERROR') {
                            swal("Error changing password")
                        }
                    })
                    .fail((resp) => {
                        $("#new-pass-loader").hide();
                        swal('Server error. Please try again later');
                    })
            } else {
                $("#new-pass-loader").hide();
                document.getElementById("new-password").focus();
            }
        }
    )
}


$("#signup-form").on('submit', (e) => {
    e.preventDefault();

    let signupFormData = $("#signup-form").serialize();
    let password = $("#password").val();
    let cPassword = $("#confirm-password").val();

    if (password === cPassword) {
        $("#signup-loader").css('display', 'flex');

        $.post("/register/signup", signupFormData)
            .done((response) => {
                $("#signup-loader").hide();
                if (response.trim() === "EXISTS") {
                    swal("Email already registered");
                } else if (response.trim() === "PASS_MISS") {
                    swal("Passwords mismatched");
                } else if (response.trim() === "REGISTERED") {
                    swal("Please verify OTP to complete registration");
                    $("#signup-form-section").hide();
                    $("#signup-otp-section").show();
                    $("#signup-otp-form").on('submit', (e) => {
                        e.preventDefault();
                        $("#signup-otp-loader").css('display', 'flex');
                        const otp = $("#signup-otp-form").serialize();
                        $.post("/register/verify-otp", otp)
                            .done((otpResp) => {
                                $("#signup-otp-loader").hide();
                                if (otpResp === "SUCCESS") {
                                    window.location = "/login";
                                } else if (otpResp === 'OTP_MISMATCHED') {
                                    swal("OTP mismatched");
                                } else if (otpResp === 'BAD_REQUEST') {
                                    swal("Bad request to server");
                                } else {
                                    swal("Error processing request");
                                }
                            })
                            .fail(() => {
                                $("#signup-otp-loader").hide();
                                swal("Server error, Please try again later");
                            })
                    })
                } else {
                    swal("Error sending email", "Please check email and try again", "failed");
                }
            })
            .fail((error) => {
                console.log(error);
                swal("Error processing your request, please try again later");
            })
    } else {
        $("#signup-loader").hide();
        swal("Password mismatched");
    }
})


const homeOfferClick = () => {
    alert("Not available right now...")
}


const navigateCategory = (url) => {
    window.open(url, "blank");
}


$("#home-search-input").on('keyup', (e) => {
    console.log("search press")
    if (e.keyCode === 13) {
        window.location = `/search/${$("#home-search-input").val().trim()}`
    }
})


$("#hidden-home-search-input").on('keyup', (e) => {
    console.log("search press")
    if (e.keyCode === 13) {
        window.location = `/search/${$("#hidden-home-search-input").val().trim()}`
    }
})


$("#add-review-form").on('submit', (e) => {
    e.preventDefault();

    $.get("/checkout/isUserLoggedIn")
        .done((resp) => {
            if (resp === true) {
                const review = $("#ta-review").val();
                const star = $("#ur-star").val();
                const pName = $("#product-name").val();

                if (review.trim() === '') {
                    swal('Please enter your review');
                } else if (parseInt(star) === 0) {
                    swal("Please select ratings star");
                } else {
                    $.post('/product/add-review', {
                        pName: pName,
                        review: review,
                        star: star
                    })
                        .done((res) => {
                            if (res === true) {
                                swal('Review added successfully');
                            } else {
                                swal('Failed to add review. Try again later');
                            }
                        })
                        .fail((err) => {
                            console.log(err)
                        })
                }
            } else {
                swal('please login to post review');
            }
        })
        .fail((resp) => {
            console.log(resp)
        })
})