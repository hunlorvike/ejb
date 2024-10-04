<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<a href="${pageContext.request.contextPath}/product?action=add" class="btn btn-primary mb-3">
    Create Product
</a>

<form action="${pageContext.request.contextPath}/index" method="get" class="mb-3">
    <div class="form-row">
        <div class="col">
            <label for="name">Name:</label>
            <input type="text" name="filterFields" id="name" placeholder="Enter product name" class="form-control">
        </div>
        <div class="col">
            <label for="price">Price:</label>
            <input type="text" name="filterFields" id="price" placeholder="Enter product price" class="form-control">
        </div>
        <div class="col">
            <button type="submit" class="btn btn-primary mt-4">Filter</button>
        </div>
    </div>
</form>

<table class="table">
    <thead>
    <tr>
        <th>ID</th>
        <th>Name</th>
        <th>Price</th>
        <th>Actions</th>
    </tr>
    </thead>
    <tbody>
    <c:choose>
        <c:when test="${not empty products}">
            <c:forEach var="product" items="${products}">
                <tr>
                    <td>${product.id}</td>
                    <td>${product.name}</td>
                    <td>${product.price}</td>
                    <td>
                        <a class="btn btn-warning"
                           href="${pageContext.request.contextPath}/product?id=${product.id}&action=edit">
                            Edit
                        </a>
                        <a class="btn btn-danger"
                           href="${pageContext.request.contextPath}/product?id=${product.id}&action=delete">
                            Delete
                        </a>
                    </td>
                </tr>
            </c:forEach>
        </c:when>
        <c:otherwise>
            <tr>
                <td colspan="4" class="text-center">No products found</td>
            </tr>
        </c:otherwise>
    </c:choose>
    </tbody>
</table>


<div class="pagination">
    <c:if test="${pageNumber > 1}">
        <a href="${pageContext.request.contextPath}/index?pageNumber=${pageNumber - 1}&pageSize=${pageSize}"
           class="btn btn-secondary">Previous</a>
    </c:if>

    <c:if test="${totalPages > 0}">
        <span>Page ${pageNumber} of ${totalPages}</span>
    </c:if>

    <c:if test="${pageNumber < totalPages}">
        <a href="${pageContext.request.contextPath}/index?pageNumber=${pageNumber + 1}&pageSize=${pageSize}"
           class="btn btn-secondary">Next</a>
    </c:if>
</div>
