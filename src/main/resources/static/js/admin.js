const showDeleteCategoryAlert = (categoryName) => {
    swal(
        {
            title: "Are you sure want to delete?",
            text: "This process can't be reversed!!",
            icon: "warning",
            buttons: true,
            dangerMode: true,
        }
    ).then((willDelete) => {
        if (willDelete) {
            window.location = `/admin/delete-category/${categoryName}`;
        } else {
            // todo later
        }
    });
}


const updateProduct = (name, description, originalPrice, discountedPrice, totalQuantities, category) => {
    swal(
        {
            title: "Are you sure want to update this product?",
            text: "",
            icon: "warning",
            buttons: true,
            dangerMode: true,
        }
    ).then((willDelete) => {
        if (willDelete) {
            const modalBody =
                `<form action="/admin/edit-product/${name}" class="mt-2" method="post" enctype="multipart/form-data">
                    <div class="container">
                        <input type="text" class="form-control" name="name" placeholder="Product Name" value="${name}" readonly>
                        <textarea class="form-control mt-2" name="description" rows="5"
                                  placeholder="Product Description">${description}</textarea>
                        <input type="number" class="form-control mt-2" min="1" name="originalPrice" value="${originalPrice}"
                               placeholder="Original Price">
                        <input type="number" class="form-control mt-2" min="1" name="discountedPrice" value="${discountedPrice}"
                               placeholder="Discounted Price">
                        <input type="number" class="form-control mt-2" min="1" name="totalQuantities" value="${totalQuantities}"
                               placeholder="Total quantities">
                        <select class="form-select mt-2" aria-label="Default select example" name="category" required>
                            <option>${category}</option>
                        </select>
                        <input class="form-control mt-2" type="file" name="image" id="formFile">
                    </div>
                    <div class="d-grid container mt-3">
                        <button type="submit" class="btn btn-outline-danger">Update</button>
                    </div>
                </form>`;

            $("#update-product-modal-body").html(modalBody);
            $('#update-product-modal').modal('show');
        } else {
            // todo later
        }
    });
}


const deleteProduct = (product) => {
    swal(
        {
            title: "Are you sure want to delete?",
            text: "This process can't be reversed!!",
            icon: "warning",
            buttons: true,
            dangerMode: true,
        }
    ).then((willDelete) => {
        if (willDelete) {
            window.location = `/admin/delete-product/${product}`;
        } else {
            // todo later
        }
    });
}


const searchProducts = () => {
    let searchKeywords = $("#search-input").val();
    if (searchKeywords.trim() !== '') {
        window.location = `/admin/manage-products/search/${searchKeywords}`;
    }
}


const searchDeliveredOrder = () => {
    const searchKeywords = $("#delivered-order-search-input").val();
    if (searchKeywords.trim() !== '') {
        window.location = `/admin/delivered-orders/search/${searchKeywords}`;
    } else {
        // todo later
    }
}


const updateOrder = (orderId) => {
    if (orderId.trim() !== '') {
        window.location = `/admin/update-order/${orderId}`;
    } else {
        swal("Something went wrong!!");
    }
}


const cancelOrder = (orderId) => {
    swal(
        {
            title: "Are you sure want to cancel order?",
            text: "This process can't be reversed!!",
            icon: "warning",
            buttons: ["No", "Yes"],
            dangerMode: true,
        }
    ).then((willDelete) => {
        if (willDelete) {
            window.location = `/admin/cancel-order/${orderId}`;
        } else {
            // todo later
        }
    });
}


const adminLogout = () => {
    swal(
        {
            title: "Are you sure want to logout?",
            text: "",
            icon: "warning",
            buttons: ["No", "Yes"],
            dangerMode: false,
        }
    ).then((yes) => {
        if (yes) {
            window.location = `/admin/logout/`;
        } else {
            // todo later
        }
    });
}


const searchOrder = () => {
    const orderInfo = $("#order-search-input").val();
    if (orderInfo.trim() !== '') {
        window.location = `/admin/pending-deliveries/search/${orderInfo}`;
    }
}


$("#search-input").on('keyup', (event) => {
    if (event.keyCode === 13) {
        searchProducts();
    }
})


$("#order-search-input").on('keyup', (event) => {
    if (event.keyCode === 13) {
        searchOrder();
    }
})


$("#delivered-order-search-input").on('keyup', (event) => {
    if (event.keyCode === 13) {
        searchDeliveredOrder();
    }
})


const updateCategory = (categoryName, categoryDescription, categoryPicture) => {
    swal(
        {
            title: "Are you sure want to update this category?",
            text: "",
            icon: "warning",
            buttons: true,
            dangerMode: true,
        }
    ).then((willDelete) => {
        if (willDelete) {
            const modalBody =
                `<form action="/admin/edit-category" class="mt-2" method="post" enctype="multipart/form-data">
                    <div class="container">
                        <input type="text" class="form-control" name="name" placeholder="Product Name" 
                        value="${categoryName}" readonly>
                        
                        <textarea class="form-control mt-2" name="description" rows="5"
                                  placeholder="Product Description">${categoryDescription}</textarea>
                        
                        <input class="form-control mt-2" type="file" name="image" id="image">
                    </div>
                    <div class="d-grid container mt-3">
                        <button type="submit" class="btn btn-outline-danger">Update</button>
                    </div>
                </form>`;

            $("#edit-category-modal-body").html(modalBody);
            $('#edit-category-modal').modal('show');
        } else {
            // todo later
        }
    });
}