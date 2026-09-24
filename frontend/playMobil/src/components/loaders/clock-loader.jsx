import { ClockLoader as Spinner } from 'react-spinners';
import { useState, useEffect } from 'react';

const CustomClockLoader = ({size}) => {
    return (
        <div className="overlay_clock">
            <div className="overlay__inner_clock">
                <div className="overlay__content_clock">
                    <Spinner color="#b99750" loading={true} size={size}/>
                </div>
            </div>
        </div>
    );
};

export default CustomClockLoader;