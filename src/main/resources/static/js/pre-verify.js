AOS.init();

const sliderImgs = [
    "../static/img/img1.jpg",
    "../static/img/img2.jpg",
    "../static/img/img3.png",
    "../static/img/img4.jpg",
    "../static/img/img5.jpg",
    "../static/img/img6.jpg",
    "../static/img/img7.png"
];

const sliderImage = document.querySelector('.background-image');
let index = 0;
let isChanging = false;

function changeImg() {
    if (isChanging) return;

    isChanging = true;
    sliderImage.classList.remove('fadeIn');
    sliderImage.classList.add('fadeOut');

    setTimeout(() => {
        index = (index + 1) % sliderImgs.length;
        sliderImage.src = sliderImgs[index];

        sliderImage.classList.remove('fadeOut');
        sliderImage.classList.add('fadeIn');

        setTimeout(() => {
            sliderImage.classList.remove('fadeIn');
            isChanging = false;
        }, 1200);
    }, 800);
}

setInterval(changeImg, 5000);

const navbar = document.querySelector('.navbar');

window.addEventListener('scroll', () => {
    if (scrollY >= 400) {
        navbar.classList.add('bg');
    } else {
        navbar.classList.remove('bg')
    }
})