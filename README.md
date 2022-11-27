# EKartuMRTScanner
Scanner E-Kartu MRT

## Setup
1. Copy IP.kt yang berada di folder projek paling luar dan di paste ke folder program utama.
2. IP.kt berfungsi untuk mengembalikan ip address database server dimana projek ini menggunakkan server localhost MariaDB di XAMPP. SQL dump projek tersedia di paling luar projek bernama e_kartu_mrt.sql. Jika menggunakkan server selain MariaDB, maka perlu menggantikkan driver JDBC yang sesuai dan mengubah dependency dan kode di Koneksi.kt.
3. Dikarenakan menggunakkan localhost, ip address yang digunakkan adalah ip address komputer yang dapat ditemukan dengan menjalankan ipconfig dalam cmd. Perhatikan ip address komputer tergantung pada koneksi internet yang terhubung sehingga jika mengubah wi-fi, maka ip address akan berubah. Jika menggunakan web server, abaikan ini dan gunakkan ip address web server tersebut.
4. Perlu diperhatikan bahwa XAMPP secara default tidak memperbolehkan remote access ke user root, baca artikel ini untuk https://www.hacking-tutorial.com/tips-and-trick/how-to-make-xampp-mysql-database-accessible-by-network/#sthash.11j7Xjht.dpbs untuk mengubahnya. Abaikan ini jika tidak menggunakkan XAMPP.
