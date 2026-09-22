import { Children, useRef, useState } from 'react';

// React reimplementation of the jQuery ceCarousel used on the leaderboards: a
// horizontal strip of cards with prev/next arrow buttons that translate the strip.
// Class names mirror the original so event-style.css styling still applies.
export default function LeaderCarousel({ id, itemWidth, gap = 25, children }) {
  const items = Children.toArray(children);
  const viewportRef = useRef(null);
  const [offset, setOffset] = useState(0);
  const step = itemWidth + gap;

  const maxOffset = () => {
    const vw = viewportRef.current ? viewportRef.current.clientWidth : 0;
    return Math.max(0, items.length * step - vw);
  };

  const prev = () => setOffset((o) => Math.max(0, o - step));
  const next = () => setOffset((o) => Math.min(maxOffset(), o + step));

  const arrowBtn = (rotate) => ({
    background: "url('/images/icons/arrow-right.svg')",
    transform: rotate ? 'rotate(180deg)' : undefined,
    width: '50px',
    height: '50px',
    border: 'none',
    backgroundSize: 'contain',
    opacity: 1,
  });

  return (
    <div className="main-leadboard-container d-flex justify-content-center" id={id}
         style={{ flexDirection: 'row', position: 'relative', width: '100%' }}>
      <div className="carousel-prev" onClick={prev}>
        <div style={{ width: '50px', height: '50px', display: 'inline-flex', alignItems: 'center' }}>
          <button style={arrowBtn(true)} className="carousel-btn" />
        </div>
      </div>
      <div className="carousel-item-inner-content" ref={viewportRef}
           style={{ position: 'relative', overflow: 'hidden', marginLeft: '5px', flex: 1 }}>
        <div className="clearfix item-container"
             style={{ display: 'flex', transform: `translate3d(${-offset}px, 0px, 0px)`, transition: 'all 0.25s ease 0s' }}>
          {items.map((child, i) => (
            <div className="carousel-items" key={i}
                 style={{ width: `${itemWidth}px`, marginRight: `${gap}px`, flex: '0 0 auto' }}>
              {child}
            </div>
          ))}
        </div>
      </div>
      <div className="carousel-next" onClick={next}>
        <div style={{ width: '50px', height: '50px', display: 'inline-flex', alignItems: 'center' }}>
          <button style={arrowBtn(false)} className="carousel-btn" />
        </div>
      </div>
    </div>
  );
}

// Same width breakpoints the ceCarousel init used.
export function carouselItemWidth(width) {
  if (width <= 375) return { itemWidth: 244, gap: 15 };
  if (width <= 425) return { itemWidth: 300, gap: 15 };
  if (width <= 768) return { itemWidth: 600, gap: 15 };
  if (width <= 1024) return { itemWidth: 900, gap: 15 };
  if (width <= 1440) return { itemWidth: 650, gap: 15 };
  if (width <= 1680) return { itemWidth: 500, gap: 25 };
  if (width <= 1778) return { itemWidth: 800, gap: 25 };
  return { itemWidth: 900, gap: 25 };
}
